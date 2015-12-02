package memorizer.freecoders.com.flashcards;

import android.app.ProgressDialog;
import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.SocketMessage;
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
    public static int EVENT_USER_ANSWER = 30;
    public static int EVENT_OPPONENT_ANSWER = 40;
    public static int EVENT_START_SESSION = 50;
    public static int EVENT_FINISH_SESSION = 60;
    public static int EVENT_USER_WAIT = 70;

    public void renderEvent (int intEventType, String strData) {
        /*
            Visualize opponent events
         */

        Log.d(LOG_TAG, "Rendering multiplayer event " + intEventType);
        if (intEventType == EVENT_USER_ANSWER) {
            Integer intAnswerID = Integer.valueOf(strData);
            MemorizerApplication.getFlashCardActivity().currentFlashCardFragment.
                    answerHighlight(intAnswerID);
        }
    }

    public void invokeEvent (int intEventType, String strData) {
        /*
            Send user events to opponent
         */

        if (currentGame == null) return;
        if (intEventType == EVENT_USER_ANSWER) {
            SocketMessage msg = new SocketMessage();
            msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_ANSWERED;
            msg.msg_body = strData;
            Set<String> players = currentGame.players.keySet();
            players.remove(MemorizerApplication.getPreferences().strSocketID);
            msg.id_to = new ArrayList(players);
            Set<String> socketIDs = currentGame.players.keySet();
            socketIDs.remove(MemorizerApplication.getPreferences().strSocketID);
            MemorizerApplication.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
        } else if (intEventType == EVENT_USER_WAIT) {
            SocketMessage msg = new SocketMessage();
            msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_STATUS_UPDATE;
            msg.msg_body = Constants.PLAYER_STATUS_WAITING;
            MemorizerApplication.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
        }
    }

    public void startGame () {
        if ((MemorizerApplication.getPreferences().strUserID != null) &&
                !MemorizerApplication.getPreferences().strUserID.isEmpty()) {
            ServerInterface.newGameRequest(MemorizerApplication.getFlashCardActivity(),
                    new Response.Listener<Game>() {
                        @Override
                        public void onResponse(Game response) {
                            if (response.status == Constants.GAME_STATUS_SEARCHING_PLAYERS) {
                                progressDialog = ProgressDialog.show(
                                        MemorizerApplication.getFlashCardActivity(),
                                        "",
                                        "Searching for opponents", true);
                            }
                        }
                    }, null);
        }
    }

}
