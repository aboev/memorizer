package memorizer.freecoders.com.flashcards;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.android.volley.Response;

import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.Question;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 26.12.15.
 */
public class GameplayManager {

    private static String LOG_TAG = "GameplayManager";

    private static Long currentSetID;

    public static ProgressDialog progressDialog;

    public static final void startSingleplayerGame(Long setID) {
        currentSetID = setID;
        FragmentManager.hideMainMenu();
        FragmentManager.showPlayersInfo();
        newLocalQuestion(null);
        Multicards.getMainActivity().intUIState = Constants.UI_STATE_TRAIN_MODE;
    }

    public static final void playAgain() {
    }

    public static final void quitSingleplayerGame() {
    }

    public static final void newServerQuestion(Question question) {
        if (Multicards.getMainActivity().intUIState != Constants.UI_STATE_MULTIPLAYER_MODE) {
            FragmentManager.hideMainMenu();
            FragmentManager.hideCardsetPickerActivity();
            Multicards.getMainActivity().intUIState = Constants.UI_STATE_MULTIPLAYER_MODE;
        }
        if ((progressDialog != null) && (progressDialog.isShowing()))
            progressDialog.dismiss();

        FlashCard mFlashcard = new FlashCard();
        mFlashcard.question = question.question;
        mFlashcard.options = question.options;
        mFlashcard.answer_id = question.answer_id;

        FlashCardFragment mFlashcardFragment = new FlashCardFragment();
        mFlashcardFragment.setActionType(FlashCardFragment.INT_SERVER_FLASHCARD);
        mFlashcardFragment.setFlashCard(mFlashcard);
        FragmentManager.showFragment(mFlashcardFragment, null);

        Multicards.getMultiplayerInterface().eventNewQuestion(question);
    }

    public static final void newLocalQuestion (Question question) {
        FlashCard mFlashcard = Multicards.getFlashCardsDAO().fetchRandomCard(currentSetID);
        final FlashCardFragment mFlashcardFragment = new FlashCardFragment();
        mFlashcardFragment.setFlashCard(mFlashcard);
        mFlashcardFragment.setActionType(mFlashcardFragment.INT_LOCAL_FLASHCARD);
        mFlashcardFragment.setOnAnswerPickListener(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                int position = (int) obj;
                FragmentManager.playersInfoFragment.intTotalQuestions++;
                if (mFlashcardFragment.mFlashCard.answer_id == position) {
                    mFlashcardFragment.listViewAdapter.
                            setCorrectAnswer(mFlashcardFragment.mFlashCard.answer_id);
                    mFlashcardFragment.listViewAdapter.notifyDataSetChanged();
                    mFlashcardFragment.numCorrectAnswers++;
                    FragmentManager.playersInfoFragment.updateScore();
                    newLocalQuestion(null);
                    FragmentManager.playersInfoFragment.increaseScore(0);
                    FragmentManager.playersInfoFragment.highlightAnswer(0, true, null);
                } else {
                    FragmentManager.playersInfoFragment.updateScore();
                    mFlashcardFragment.wrongAnswerNotify();
                    showAnswer(position);
                    FragmentManager.playersInfoFragment.highlightAnswer(0, false, null);
                }
                mFlashcardFragment.setEmptyOnFlashcardItemClickListener();
            }
        });
        FragmentManager.showFragment(mFlashcardFragment, null);
    }

    public static void showAnswer (int intWrongAnswerID) {
        final FlashCardFragment newFlashCardFragment = new FlashCardFragment();
        newFlashCardFragment.setActionType(FlashCardFragment.INT_SHOW_ANSWER);
        FragmentManager.currentFlashCardFragment.getFlashCard().wrong_answer_id = intWrongAnswerID;
        newFlashCardFragment.setFlashCard(FragmentManager.currentFlashCardFragment.getFlashCard());
        FragmentManager.showFragment(newFlashCardFragment, Constants.ANIMATION_FLIP);
        newFlashCardFragment.setOnAnswerPickListener(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                newLocalQuestion(null);
                newFlashCardFragment.setEmptyOnFlashcardItemClickListener();
            }
        });
    }

    public static final void requestMultiplayerGame (final String strGID) {
        if ((Multicards.getPreferences().strUserID == null) ||
                Multicards.getPreferences().strUserID.isEmpty())
            return;

        if ((Multicards.getPreferences().strUserName == null) ||
                Multicards.getPreferences().strUserName.isEmpty())
            InputDialogInterface.updateUserName(new CallbackInterface() {
                @Override
                public void onResponse(Object obj) {
                ServerInterface.newGameRequest(strGID,
                    new Response.Listener<Game>() {
                        @Override
                        public void onResponse(Game response) {
                            if (response.status == Constants.GAME_STATUS_SEARCHING_PLAYERS) {
                                Multicards.getMultiplayerInterface().setGameData(null, strGID);
                                String strMessage = Multicards.getMainActivity().
                                        getResources().getString(
                                        R.string.waiting_opponent_dialog_message);
                                progressDialog = ProgressDialog.show(
                                        Multicards.getMainActivity(), "", strMessage, true);
                                progressDialog.setOnCancelListener(
                                        new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        Multicards.getMultiplayerInterface().quitGame();
                                    }
                                });
                            }
                            progressDialog.setCancelable(true);
                        }
                    }, null);
                }
            });
        else
            ServerInterface.newGameRequest(strGID,
                new Response.Listener<Game>() {
                    @Override
                    public void onResponse(Game response) {
                        if (response.status == Constants.GAME_STATUS_SEARCHING_PLAYERS) {
                            Multicards.getMultiplayerInterface().setGameData(null, strGID);
                            String strMessage = Multicards.getMainActivity().
                                    getResources().getString(
                                    R.string.waiting_opponent_dialog_message);
                            progressDialog = ProgressDialog.show(
                                    Multicards.getMainActivity(),
                                    "",
                                    strMessage, true);
                            progressDialog.setCancelable(true);
                            progressDialog.setOnCancelListener(
                                    new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            Multicards.getMultiplayerInterface().quitGame();
                                        }
                                    });
                        }
                    }
                }, null);
    }

    public static final void startMultiplayerGame(Game game) {
        Multicards.getMultiplayerInterface().setGameData(game, null);
        FragmentManager.showPlayersInfo();
        FragmentManager.playersInfoFragment.updateGameInfo(game);

    }

    public static final void quitMultilayerGame() {
        Multicards.getMainActivity().returnToMainMenu();
        InputDialogInterface.showGameOverMessage(null);
        Multicards.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;
    }

}
