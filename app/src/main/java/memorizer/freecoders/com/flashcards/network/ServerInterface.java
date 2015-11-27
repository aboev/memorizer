package memorizer.freecoders.com.flashcards.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.json.ServerResponse;
import memorizer.freecoders.com.flashcards.json.UserDetails;

/**
 * Created by alex-mac on 21.11.15.
 */
public class ServerInterface {

    private static Gson gson = new Gson();

    private static String LOG_TAG = "ServerInterface";

    private Socket mSocketIO;
    private String strSocketID;

    public static final void registerUserRequest(Context context,
                                                UserDetails userDetails,
                                                final Response.Listener<String> responseListener,
                                                final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        Log.d(LOG_TAG, "Register user request");
        StringRequest request = new StringRequest(Request.Method.POST,
                Constants.SERVER_URL + Constants.SERVER_PATH_USER ,
                gson.toJson(userDetails), headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        Type type = new TypeToken<ServerResponse<String>>(){}.getType();
                        try {
                            ServerResponse<String> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.isSuccess() && res.data != null
                                    && !res.data.isEmpty()
                                    && responseListener != null)
                                responseListener.onResponse(res.data);
                            else if (errorListener != null)
                                errorListener.onErrorResponse(new VolleyError());
                        } catch (Exception e) {
                            Log.d(LOG_TAG, "Exception: " + e.getLocalizedMessage());
                            if (errorListener != null) errorListener.onErrorResponse(
                                    new VolleyError());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorListener != null) errorListener.onErrorResponse(error);
            }
        }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void setSocketIO (Socket socket){
        this.mSocketIO = socket;
    }

    public Socket getSocketIO () {
        return this.mSocketIO;
    }

    public void setSocketID (String strSocketID) {
        this.strSocketID = strSocketID;
    }

    public String getSocketID () {
        return this.strSocketID;
    }

    public Emitter.Listener onNewSocketMessage = new Emitter.Listener() {
        @Override
        public void call(final Object[] args) {
            Log.d(LOG_TAG, "Received message " + args[0].toString());
            MemorizerApplication.getFlashCardActivity().
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
/*
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
*/
                }
            });
        }
    };

    private static HashMap<String, String> makeHTTPHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "*/*");
        headers.put(Constants.HEADER_USERID, MemorizerApplication.getPreferences().strUserID);
        return headers;
    }
}
