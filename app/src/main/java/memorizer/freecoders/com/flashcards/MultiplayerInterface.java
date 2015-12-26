package memorizer.freecoders.com.flashcards;

import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.android.volley.Response;
import com.google.gson.Gson;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.Question;
import memorizer.freecoders.com.flashcards.json.SocketMessage;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 21.11.15.
 */
public class MultiplayerInterface {

    private static String LOG_TAG = "MultiPlayerInterface";

    public ProgressDialog progressDialog;

    public Game currentGame;
    private Question currentQuestion;
    private int currentAnswer;
    private String strUserStatus;
    private Boolean boolAnswerConfirmed;

    Gson gson = new Gson();

    public static int EVENT_INCOMING_INVITATION = 0;
    public static int EVENT_INVITATION_ACCEPTED = 10;
    public static int EVENT_NEW_QUESTION = 20;
    public static int EVENT_USER_ANSWER = 30;   // User answered question
    public static int EVENT_USER_THINK = 40;   // User think
    public static int EVENT_OPPONENT_ANSWER = 50;
    public static int EVENT_START_SESSION = 60;
    public static int EVENT_FINISH_SESSION = 70;
    public static int EVENT_USER_WAIT = 80;     // User ready for next question

    public void invokeEvent (int intEventType, String strData) {
        /*
            Send user events to opponent
         */

        if (currentGame == null) return;
        if (intEventType == EVENT_USER_ANSWER) {    // User answered
            SocketMessage msg = new SocketMessage();
            msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_ANSWERED;
            msg.msg_body = strData;
            msg.id_to = new ArrayList();
            Multicards.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
            eventUserAnswer(Integer.valueOf(strData));
        } else if (intEventType == EVENT_USER_WAIT) {   // User ready for next question
            SocketMessage msg = new SocketMessage();
            msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_STATUS_UPDATE;
            msg.msg_body = Constants.PLAYER_STATUS_WAITING;
            Multicards.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
        } else if (intEventType == EVENT_USER_THINK) {   // User ready for next question
            SocketMessage msg = new SocketMessage();
            msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_STATUS_UPDATE;
            msg.msg_body = Constants.PLAYER_STATUS_THINKING;
            Multicards.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
        }
    }

    public void quitGame () {
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_QUIT_GAME;
        msg.msg_body = "";
        Multicards.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
    }

    public void requestNewGame(final String strGID) {
        if ((Multicards.getPreferences().strUserID != null) &&
                !Multicards.getPreferences().strUserID.isEmpty()) {

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
                                        progressDialog = ProgressDialog.show(
                                                Multicards.getMainActivity(),
                                                "",
                                                "Searching for opponents", true);
                                        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                quitGame();
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
                                    progressDialog = ProgressDialog.show(
                                            Multicards.getMainActivity(),
                                            "",
                                            "Searching for opponents", true);
                                    progressDialog.setCancelable(true);
                                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            quitGame();
                                        }
                                    });
                                }
                            }
                }, null);
        }
    }

    public void setCurrentQuestion(Question question) {
        Question newQuestion = new Question();
        newQuestion.question = question.question;
        newQuestion.options = new ArrayList<String>();
        newQuestion.options.addAll(question.options);
        newQuestion.answer_id = question.answer_id;
        newQuestion.question_id = question.question_id;
        currentQuestion = newQuestion;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void eventUserAnswer(int answerID) {
        currentAnswer = answerID;
        strUserStatus = Constants.PLAYER_STATUS_ANSWERED;
    }

    public void eventAnswerAccepted (int questionID) {
        if ((currentQuestion != null) && (currentQuestion.question_id == questionID)) {
            Multicards.getMainActivity().currentFlashCardFragment.
                answerHighlight(currentAnswer, false, null);
            CallbackInterface onAnimationEnd = new CallbackInterface() {
                @Override
                public void onResponse(Object obj) {
                    invokeEvent(EVENT_USER_WAIT, "");
                }
            };
            if (Multicards.getMainActivity().currentFlashCardFragment.mFlashCard.answer_id
                    == currentAnswer) {
                Multicards.getMainActivity().playersInfoFragment.increaseScore(0);
                Multicards.getMainActivity().playersInfoFragment.highlightAnswer(0, true,
                        onAnimationEnd);
            } else {
                Multicards.getMainActivity().playersInfoFragment.highlightAnswer(0, false,
                        onAnimationEnd);
            }
            Multicards.getMainActivity().playersInfoFragment.updateScore();
            strUserStatus = Constants.PLAYER_STATUS_ANSWERED;
        }
        boolAnswerConfirmed = true;
    }

    public void eventAnswerRejected(int questionID) {
        invokeEvent(EVENT_USER_WAIT, "");
        boolAnswerConfirmed = true;
    }

    public void eventOpponentAnswer(String strAnswerID) {
        Integer intAnswerID = Integer.valueOf(strAnswerID);
        Multicards.getMainActivity().currentFlashCardFragment.
            answerHighlight(intAnswerID, true, null);
        if (Multicards.getMainActivity().currentFlashCardFragment.
                mFlashCard.answer_id == intAnswerID) {
            CallbackInterface onAnimationEnd = null;
            if (boolAnswerConfirmed || (!strUserStatus.equals(Constants.PLAYER_STATUS_ANSWERED))) {
                onAnimationEnd = new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        invokeEvent(EVENT_USER_WAIT, "");
                    }
                };
                Multicards.getMainActivity().currentFlashCardFragment.
                        setEmptyOnFlashcardItemClickListener();
            }
            Multicards.getMainActivity().playersInfoFragment.highlightAnswer(1, true,
                    onAnimationEnd);
            Multicards.getMainActivity().playersInfoFragment.increaseScore(1);
            Multicards.getMainActivity().playersInfoFragment.updateScore();
        } else
            Multicards.getMainActivity().playersInfoFragment.highlightAnswer(1, false, null);
    }

    public void eventNewQuestion (Question question) {
        if ((progressDialog != null) && (progressDialog.isShowing()))
            progressDialog.dismiss();
        Multicards.getMainActivity().nextFlashCard(question);
        invokeEvent(Multicards.getMultiplayerInterface().EVENT_USER_THINK, "");
        Multicards.getMainActivity().intUIState = Constants.UI_STATE_MULTIPLAYER_MODE;
        setCurrentQuestion(question);
        strUserStatus = Constants.PLAYER_STATUS_THINKING;
        boolAnswerConfirmed = false;
        Multicards.getMainActivity().playersInfoFragment.updateScore();
    }

}
