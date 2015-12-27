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
import memorizer.freecoders.com.flashcards.network.SocketInterface;

/**
 * Created by alex-mac on 21.11.15.
 */
public class MultiplayerInterface {

    private static String LOG_TAG = "MultiPlayerInterface";

    public ProgressDialog progressDialog;

    public GameData currentGame;
    private int currentAnswer;

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

    public void invokeEvent (int intEventType, String strData) {    // Deliver events to server
        if (currentGame == null) return;
        if (intEventType == EVENT_USER_ANSWER) {    // User answered
            SocketInterface.emitPlayerAnswered(strData);
            eventUserAnswer(Integer.valueOf(strData));
        } else if (intEventType == EVENT_USER_WAIT) {   // User ready for next question
            SocketInterface.emitStatusUpdate(Constants.PLAYER_STATUS_WAITING);
        } else if (intEventType == EVENT_USER_THINK) {   // User ready for next question
            SocketInterface.emitStatusUpdate(Constants.PLAYER_STATUS_THINKING);
        }
    }

    public void quitGame () {
        currentGame = null;
        SocketInterface.emitQuitGame();
    }

    public void eventUserAnswer(int answerID) {
        currentAnswer = answerID;
        currentGame.strUserStatus = Constants.PLAYER_STATUS_ANSWERED;
    }

    public void eventAnswerAccepted (int questionID) {
        if ((currentGame.currentQuestion != null) &&
                (currentGame.currentQuestion.question_id == questionID)) {
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
            currentGame.strUserStatus = Constants.PLAYER_STATUS_ANSWERED;
        }
        currentGame.boolAnswerConfirmed = true;
    }

    public void eventAnswerRejected(int questionID) {
        invokeEvent(EVENT_USER_WAIT, "");
        currentGame.boolAnswerConfirmed = true;
    }

    public void eventOpponentAnswer(String strAnswerID) {
        Integer intAnswerID = Integer.valueOf(strAnswerID);
        Multicards.getMainActivity().currentFlashCardFragment.
            answerHighlight(intAnswerID, true, null);
        if (Multicards.getMainActivity().currentFlashCardFragment.mFlashCard.answer_id
                == intAnswerID) {
            CallbackInterface onAnimationEnd = null;
            if (currentGame.boolAnswerConfirmed ||
                    (!currentGame.strUserStatus.equals(Constants.PLAYER_STATUS_ANSWERED))) {
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
        Multicards.getMainActivity().nextFlashCard(question);

        SocketInterface.emitStatusUpdate(Constants.PLAYER_STATUS_THINKING);

        if (currentGame == null)
            currentGame = new GameData();
        currentGame.setCurrentQuestion(question);
        currentGame.strUserStatus = Constants.PLAYER_STATUS_THINKING;
        currentGame.boolAnswerConfirmed = false;
        Multicards.getMainActivity().playersInfoFragment.updateScore();
    }

    public void setGameData(Game game, String strGID) {
        if (currentGame == null)
            currentGame = new GameData();
        if (game != null)
            currentGame.game = game;
        if (strGID != null)
            currentGame.strGID = strGID;
    }

    public class GameData {
        public Game game;
        public String strGID = "";
        public String strUserStatus = "";
        public Question currentQuestion;
        public Boolean boolAnswerConfirmed = false;

        public void setCurrentQuestion (Question question) {
            Question newQuestion = new Question();
            newQuestion.question = question.question;
            newQuestion.options = new ArrayList<String>();
            newQuestion.options.addAll(question.options);
            newQuestion.answer_id = question.answer_id;
            newQuestion.question_id = question.question_id;
            currentQuestion = newQuestion;
        }
    }

}
