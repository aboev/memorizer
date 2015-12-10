package memorizer.freecoders.com.flashcards;

import android.app.ProgressDialog;
import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Set;

import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.json.Game;
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

    public void renderEvent (int intEventType, String strData) {
        /*
            Visualize opponent events
         */

        Log.d(LOG_TAG, "Rendering multiplayer event " + intEventType);
        if (intEventType == EVENT_USER_ANSWER) {    // Opponent answered
            Integer intAnswerID = Integer.valueOf(strData);
            MemorizerApplication.getMainActivity().currentFlashCardFragment.
                    answerHighlight(intAnswerID, true);
            if (MemorizerApplication.getMainActivity().currentFlashCardFragment.
                    mFlashCard.answer_id == intAnswerID) {
                MemorizerApplication.getMainActivity().playersInfoFragment.increaseScore(1);
                MemorizerApplication.getMainActivity().playersInfoFragment.updateScore();
            }
        }
    }

    public void invokeEvent (int intEventType, String strData) {
        /*
            Send user events to opponent
         */

        if (currentGame == null) return;
        if (intEventType == EVENT_USER_ANSWER) {    // User answered
            SocketMessage msg = new SocketMessage();
            msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_ANSWERED;
            msg.msg_body = strData;
            Set<String> players = currentGame.players.keySet();
            players.remove(MemorizerApplication.getPreferences().strSocketID);
            msg.id_to = new ArrayList(players);
            Set<String> socketIDs = currentGame.players.keySet();
            socketIDs.remove(MemorizerApplication.getPreferences().strSocketID);
            MemorizerApplication.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
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

    public void requestNewGame() {
        if ((MemorizerApplication.getPreferences().strUserID != null) &&
                !MemorizerApplication.getPreferences().strUserID.isEmpty()) {

            if ((MemorizerApplication.getPreferences().strUserName == null) ||
                    MemorizerApplication.getPreferences().strUserName.isEmpty())
                InputDialogInterface.updateUserName(new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        ServerInterface.newGameRequest(null,
                            new Response.Listener<Game>() {
                                @Override
                                public void onResponse(Game response) {
                                    if (response.status == Constants.GAME_STATUS_SEARCHING_PLAYERS) {
                                        progressDialog = ProgressDialog.show(
                                                MemorizerApplication.getMainActivity(),
                                                "",
                                                "Searching for opponents", true);
                                    }
                                }
                            }, null);
                    }
                });
            else
                ServerInterface.newGameRequest(null,
                        new Response.Listener<Game>() {
                            @Override
                            public void onResponse(Game response) {
                                if (response.status == Constants.GAME_STATUS_SEARCHING_PLAYERS) {
                                    progressDialog = ProgressDialog.show(
                                            MemorizerApplication.getMainActivity(),
                                            "",
                                            "Searching for opponents", true);
                                }
                            }
                }, null);
        }
    }

}
