package memorizer.freecoders.com.flashcards;

import android.app.ProgressDialog;

import com.android.volley.Response;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 21.11.15.
 */
public class MultiplayerInterface {
    public ProgressDialog progressDialog;

    public static int EVENT_INCOMING_INVITATION = 0;
    public static int EVENT_INVITATION_ACCEPTED = 10;
    public static int EVENT_NEW_QUESTION = 20;
    public static int EVENT_USER_ANSWER = 30;
    public static int EVENT_OPPONENT_ANSWER = 40;
    public static int EVENT_START_SESSION = 50;
    public static int EVENT_FINISH_SESSION = 60;

    public void renderEvent (int intEventType, String strData) {

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
