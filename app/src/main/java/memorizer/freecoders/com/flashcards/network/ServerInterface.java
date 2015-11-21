package memorizer.freecoders.com.flashcards.network;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import memorizer.freecoders.com.flashcards.common.MemorizerApplication;

/**
 * Created by alex-mac on 21.11.15.
 */
public class ServerInterface {

    public Emitter.Listener onNewSocketMessage = new Emitter.Listener() {
        @Override
        public void call(final Object[] args) {
            MemorizerApplication.getFlashCardActivity().
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}
