package memorizer.freecoders.com.flashcards;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Random;

import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.classes.GameplayData;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.fragments.FlashCardFragment;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.GameOverMessage;
import memorizer.freecoders.com.flashcards.json.InvitationDescriptor;
import memorizer.freecoders.com.flashcards.json.Question;
import memorizer.freecoders.com.flashcards.json.ServerResponse;
import memorizer.freecoders.com.flashcards.json.SocketMessage;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.network.SocketInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 26.12.15.
 */
public class GameplayManager {

    private static String LOG_TAG = "GameplayManager";

    private static Long currentSetID;
    private static String currentGID = "";
    private static int intQuestionCount = 0;
    public static String strOpponentName = null;
    public static GameplayData currentGameplay;

    public static ProgressDialog progressDialog;

    private static HashMap<Integer, Long> latencyTimestamps = new HashMap<Integer, Long>();

    public static final void startSingleplayerGame(Long setID, String strGID) {
        currentSetID = setID;
        currentGameplay = new GameplayData(strGID, GameplayData.INT_SINGLEPLAYER);
        if (!currentGameplay.boolCardsetComplete()) {
            InputDialogInterface.showModalDialog(Multicards.getMainActivity().
                    getResources().getString(R.string.string_empty_cardset), null);
            return;
        }
        FragmentManager.showGamePlayFragments(false, Constants.UI_STATE_TRAIN_MODE);
        newLocalQuestion(currentGameplay.getNextQuestion());
        currentGID = strGID;
        intQuestionCount = 0;
    }

    public static final void playAgain() {
    }

    public static final void quitSingleplayerGame() {
        FragmentManager.showGameOverFragment(currentGID, null, false);
    }

    public static final void newServerQuestion(Question question, HashMap<String, Integer> scores) {
        if (FragmentManager.intUIState != Constants.UI_STATE_MULTIPLAYER_MODE) {
            FragmentManager.hideMainMenu();
            FragmentManager.hideCardsetPickerActivity();
            FragmentManager.intUIState = Constants.UI_STATE_MULTIPLAYER_MODE;
        }
        if ((progressDialog != null) && (progressDialog.isShowing()))
            progressDialog.dismiss();

        if ((InputDialogInterface.progressDialog != null)
                && (InputDialogInterface.progressDialog.isShowing()))
            InputDialogInterface.progressDialog.dismiss();

        FlashCard mFlashcard = new FlashCard();
        mFlashcard.question = question.question;
        mFlashcard.options = question.options;
        mFlashcard.answer_id = question.answer_id;
        currentGameplay.newServerQuestion(mFlashcard);

        final FlashCardFragment mFlashcardFragment = new FlashCardFragment();
        mFlashcardFragment.setFlashCard(mFlashcard);
        mFlashcardFragment.setActionType(FlashCardFragment.INT_SERVER_FLASHCARD);
        mFlashcardFragment.setOnAnswerPickListener(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                Integer position = (Integer) obj;
                Multicards.getMultiplayerInterface().invokeEvent(
                        Multicards.getMultiplayerInterface().EVENT_USER_ANSWER,
                        String.valueOf(position));
                mFlashcardFragment.setEmptyOnFlashcardItemClickListener();
            }
        });
        FragmentManager.showFragment(mFlashcardFragment, null);
        currentGameplay.setAnswer(-1);
        //checkNetworkLatency();

        Multicards.getMultiplayerInterface().eventNewQuestion(question, scores);
    }

    public static final void newLocalQuestion (FlashCard mFlashcard) {
        if (mFlashcard == null) {
            quitSingleplayerGame();
            return;
        }
        final FlashCardFragment mFlashcardFragment = new FlashCardFragment();
        mFlashcardFragment.setFlashCard(mFlashcard);
        mFlashcardFragment.setActionType(mFlashcardFragment.INT_LOCAL_FLASHCARD);
        mFlashcardFragment.setOnAnswerPickListener(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                intQuestionCount++;
                Log.d(LOG_TAG, "Question count " + intQuestionCount);
                int position = (int) obj;
                currentGameplay.setAnswer(position);
                if (mFlashcardFragment.mFlashCard.answer_id == position) {
                    mFlashcardFragment.listViewAdapter.
                            setCorrectAnswer(mFlashcardFragment.mFlashCard.answer_id);
                    mFlashcardFragment.listViewAdapter.notifyDataSetChanged();
                    FragmentManager.playersInfoFragment.eventPlayer1Answer(true);
                    FragmentManager.playersInfoFragment.updateInfo();
                    newLocalQuestion(currentGameplay.getNextQuestion());
                    FragmentManager.playersInfoFragment.highlightAnswer(0, true, null);
                    Utils.vibrateShort();
                } else {
                    FragmentManager.playersInfoFragment.eventPlayer1Answer(false);
                    FragmentManager.playersInfoFragment.updateInfo();
                    //mFlashcardFragment.wrongAnswerNotify();
                    showAnswer(position);
                    FragmentManager.playersInfoFragment.highlightAnswer(0, false, null);
                    Utils.vibrateLong();
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
                newLocalQuestion(currentGameplay.getNextQuestion());
                newFlashCardFragment.setEmptyOnFlashcardItemClickListener();
            }
        });
    }

    public static final void requestMultiplayerGame(final String strOpponentName,
            final String strGID) {
        if ((Multicards.getPreferences().strUserID == null) ||
                Multicards.getPreferences().strUserID.isEmpty())
            return;

        String strMessage = Multicards.getMainActivity().getResources().getString(
                R.string.string_starting_game);
        InputDialogInterface.showProgressBar(strMessage, null);

        ServerInterface.startGameRequest(true, strGID, strOpponentName,
            new Response.Listener<ServerResponse<Game>>() {
                @Override
                public void onResponse(ServerResponse<Game> res) {
                    Gson gson = new Gson();

                    if (res.isSuccess()) {
                        Game response = res.data;
                        if ((response.status == Constants.GAME_STATUS_SEARCHING_PLAYERS) ||
                                (response.status == Constants.GAME_STATUS_WAITING_OPPONENT)) {
                            Multicards.getMultiplayerInterface().setGameData(null, strGID);

                            InputDialogInterface.hideProgressBar();
                            String strMessage = "";
                            if ((strOpponentName == null) || strOpponentName.isEmpty())
                                strMessage = Multicards.getMainActivity().getResources().getString(
                                        R.string.waiting_opponent_dialog_message);
                            else
                                strMessage = Multicards.getMainActivity().getResources().getString(
                                        R.string.string_inviting_opponent);

                            InputDialogInterface.showProgressBar(strMessage, new CallbackInterface() {
                                @Override
                                public void onResponse(Object obj) {
                                    Multicards.getMultiplayerInterface().quitGame();
                                    FragmentManager.setUIStates.
                                            remove(Constants.UI_DIALOG_WAITING_OPPONENT);
                                }
                            });

                            FragmentManager.setUIStates.add(Constants.UI_DIALOG_WAITING_OPPONENT);
                        }
                    } else {
                        InputDialogInterface.hideProgressBar();
                        InputDialogInterface.deliverError(res);
                        FragmentManager.setUIStates.remove(Constants.UI_DIALOG_WAITING_OPPONENT);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    InputDialogInterface.hideProgressBar();
                    FragmentManager.setUIStates.remove(Constants.UI_DIALOG_WAITING_OPPONENT);
                }
            });
    }

    public static final void startMultiplayerGame(Game game) {
        FragmentManager.setUIStates.add(Constants.UI_STATE_MULTIPLAYER_MODE);
        FragmentManager.intUIState = Constants.UI_STATE_MULTIPLAYER_MODE;
        currentGameplay = new GameplayData(null, GameplayData.INT_MULTIPLAYER);
        Multicards.getMultiplayerInterface().setGameData(game, null);
        Multicards.getPreferences().saveRecentOpponent(Utils.extractOpponentProfile(game.profiles));
        FragmentManager.showPlayersInfo(false);
        FragmentManager.playersInfoFragment.initInfo();
        Multicards.getMainActivity().linearLayoutEmoticons.setVisibility(View.VISIBLE);
        FragmentManager.initEmoticons();
    }

    public static final void quitMultilayerGame(GameOverMessage gameOverMessage) {
        FragmentManager.showGameOverFragment(Multicards.
                getMultiplayerInterface().currentGame.strGID, gameOverMessage, false);
    }

    public static final void stopMultilayerGame(Boolean boolConfigurationChange) {
        FragmentManager.returnToMainMenu(boolConfigurationChange);
        InputDialogInterface.showGameStopMessage(null);
        FragmentManager.intUIState = Constants.UI_STATE_MAIN_MENU;
    }

    public static final void networkLatencyCallback (int value) {
        Long intLatency =  System.currentTimeMillis() - latencyTimestamps.get(value);
        if (intLatency > 200)
            Multicards.getMainActivity().textViewNetworkState.setVisibility(View.VISIBLE);
        else
            Multicards.getMainActivity().textViewNetworkState.setVisibility(View.GONE);
    }

    public static final void gameInvitation (final InvitationDescriptor invitation) {

        if (!Multicards.getMainActivity().boolIsForeground) {
            Intent intent = new Intent(Multicards.getMainActivity(),
                    MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(Multicards.getMainActivity(), 0, intent, 0);
            InputDialogInterface.showInvitationNotification(invitation, pi,
                    Multicards.getMainActivity());
        }

        final Integer game_id;
        if ((invitation != null) && (invitation.game != null) &&
                (invitation.game.game_id != null))
            game_id = Integer.valueOf(invitation.game.game_id);
        else
            game_id = -1;
        InputDialogInterface.showInvitationDialog(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                if (game_id != -1)
                    SocketInterface.emitInvitationAccepted(game_id,
                            new CallbackInterface() {
                                @Override
                                public void onResponse(Object obj) {
                                    if (Multicards.getMainActivity().boolIsForeground)
                                        SocketInterface.emitStatusUpdate(Constants.PLAYER_STATUS_WAITING);
                                }
                            }, invitation);

                InputDialogInterface.showProgressBar(Multicards.getMainActivity().
                                getResources().getString(R.string.string_waiting_opponent),
                        new CallbackInterface() {
                            @Override
                            public void onResponse(Object obj) {
                                Multicards.getMultiplayerInterface().quitGame();
                            }
                        });

                Multicards.getPreferences().setRecentCardset(invitation);
            }
        }, new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                if (game_id != -1)
                    SocketInterface.emitInvitationRejected(game_id);
            }
        }, invitation);
    }

    public static final void invitationAccepted(Integer intGameID) {
        if ((FragmentManager.setUIStates.contains(Constants.UI_DIALOG_WAITING_OPPONENT))
                && Multicards.getMainActivity().boolIsForeground) {
            InputDialogInterface.hideProgressBar();
            SocketInterface.emitStatusUpdate(Constants.PLAYER_STATUS_WAITING);
            FragmentManager.setUIStates.remove(Constants.UI_DIALOG_WAITING_OPPONENT);
        }
    }

    public static final void invitationRejected(Integer intGameID) {
        if ((FragmentManager.setUIStates.contains(Constants.UI_DIALOG_WAITING_OPPONENT))
                && Multicards.getMainActivity().boolIsForeground) {
            InputDialogInterface.hideProgressBar();
            FragmentManager.setUIStates.remove(Constants.UI_DIALOG_WAITING_OPPONENT);
            String strMessage = Multicards.getMainActivity().
                    getResources().getString(R.string.string_opponent_busy);
            InputDialogInterface.showModalDialog(strMessage, null);
        }
    }

    public static final void statusUpdated(String status) {
        if ((FragmentManager.setUIStates.contains(Constants.UI_DIALOG_WAITING_OPPONENT))
                && Multicards.getMainActivity().boolIsForeground) {
            InputDialogInterface.progressDialog.dismiss();
            SocketInterface.emitStatusUpdate(Constants.PLAYER_STATUS_WAITING);
        }
    }

    public static final void checkNetworkLatency () {
        Random rn = new Random();
        Integer randomInt = rn.nextInt(100000);
        latencyTimestamps.put(randomInt, System.currentTimeMillis());
        SocketInterface.emitCheckNetwork(randomInt);
    }

}
