package memorizer.freecoders.com.flashcards;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;

import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.Question;
import memorizer.freecoders.com.flashcards.json.SocketMessage;
import memorizer.freecoders.com.flashcards.json.UserDetails;
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
            MemorizerApplication.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
            eventUserAnswer(Integer.valueOf(strData));
        } else if (intEventType == EVENT_USER_WAIT) {   // User ready for next question
            SocketMessage msg = new SocketMessage();
            msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_STATUS_UPDATE;
            msg.msg_body = Constants.PLAYER_STATUS_WAITING;
            MemorizerApplication.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
        } else if (intEventType == EVENT_USER_THINK) {   // User ready for next question
            SocketMessage msg = new SocketMessage();
            msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_STATUS_UPDATE;
            msg.msg_body = Constants.PLAYER_STATUS_THINKING;
            MemorizerApplication.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
        }
    }

    public void quitGame () {
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_QUIT_GAME;
        msg.msg_body = "";
        MemorizerApplication.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
    }

    public void requestNewGame(final String strGID) {
        if ((MemorizerApplication.getPreferences().strUserID != null) &&
                !MemorizerApplication.getPreferences().strUserID.isEmpty()) {

            if ((MemorizerApplication.getPreferences().strUserName == null) ||
                    MemorizerApplication.getPreferences().strUserName.isEmpty())
                InputDialogInterface.updateUserName(new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        ServerInterface.newGameRequest(strGID,
                            new Response.Listener<Game>() {
                                @Override
                                public void onResponse(Game response) {
                                    if (response.status == Constants.GAME_STATUS_SEARCHING_PLAYERS) {
                                        progressDialog = ProgressDialog.show(
                                                MemorizerApplication.getMainActivity(),
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
                                            MemorizerApplication.getMainActivity(),
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
            MemorizerApplication.getMainActivity().currentFlashCardFragment.
                answerHighlight(currentAnswer, false, null);
            CallbackInterface onAnimationEnd = new CallbackInterface() {
                @Override
                public void onResponse(Object obj) {
                    invokeEvent(EVENT_USER_WAIT, "");
                }
            };
            if (MemorizerApplication.getMainActivity().currentFlashCardFragment.mFlashCard.answer_id
                    == currentAnswer) {
                MemorizerApplication.getMainActivity().playersInfoFragment.increaseScore(0);
                MemorizerApplication.getMainActivity().playersInfoFragment.highlightAnswer(0, true,
                        onAnimationEnd);
            } else {
                MemorizerApplication.getMainActivity().playersInfoFragment.highlightAnswer(0, false,
                        onAnimationEnd);
            }
            MemorizerApplication.getMainActivity().playersInfoFragment.updateScore();
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
        MemorizerApplication.getMainActivity().currentFlashCardFragment.
            answerHighlight(intAnswerID, true, null);
        if (MemorizerApplication.getMainActivity().currentFlashCardFragment.
                mFlashCard.answer_id == intAnswerID) {
            CallbackInterface onAnimationEnd = null;
            if (boolAnswerConfirmed || (!strUserStatus.equals(Constants.PLAYER_STATUS_ANSWERED))) {
                onAnimationEnd = new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        invokeEvent(EVENT_USER_WAIT, "");
                    }
                };
                MemorizerApplication.getMainActivity().currentFlashCardFragment.
                        setEmptyOnFlashcardItemClickListener();
            }
            MemorizerApplication.getMainActivity().playersInfoFragment.highlightAnswer(1, true,
                    onAnimationEnd);
            MemorizerApplication.getMainActivity().playersInfoFragment.increaseScore(1);
            MemorizerApplication.getMainActivity().playersInfoFragment.updateScore();
        } else
            MemorizerApplication.getMainActivity().playersInfoFragment.highlightAnswer(1, false, null);
    }

    public void eventNewQuestion (Question question) {
        if ((progressDialog != null) && (progressDialog.isShowing()))
            progressDialog.dismiss();
        MemorizerApplication.getMainActivity().nextFlashCard(question);
        invokeEvent(MemorizerApplication.getMultiplayerInterface().EVENT_USER_THINK, "");
        MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_MULTIPLAYER_MODE;
        setCurrentQuestion(question);
        strUserStatus = Constants.PLAYER_STATUS_THINKING;
        boolAnswerConfirmed = false;
        MemorizerApplication.getMainActivity().playersInfoFragment.updateScore();
    }

}
