package memorizer.freecoders.com.flashcards.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import memorizer.freecoders.com.flashcards.MainMenuFragment;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.ConstantsPrivate;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.CardSet;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.Question;
import memorizer.freecoders.com.flashcards.json.ServerResponse;
import memorizer.freecoders.com.flashcards.json.SocketMessage;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletSearchResult;

/**
 * Created by alex-mac on 21.11.15.
 */
public class ServerInterface {

    private static Gson gson = new Gson();

    private static String LOG_TAG = "ServerInterface";

    private Socket mSocketIO;
    private String strSocketID;

    public static final void registerUserRequest(
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
                        Type type = new TypeToken<ServerResponse
                                <HashMap<String,String>>>(){}.getType();
                        try {
                            ServerResponse<HashMap<String, String>> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.isSuccess() && res.data != null
                                    && res.data.containsKey(Constants.KEY_ID)
                                    && responseListener != null)
                                responseListener.onResponse(res.data.get(Constants.KEY_ID));
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
        VolleySingleton.getInstance(Multicards.getMainActivity()).
                addToRequestQueue(request);
    }

    public static final void updateUserDetailsRequest(
             UserDetails userDetails,
             final Response.Listener<String> responseListener,
             final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        Log.d(LOG_TAG, "Update user details request");
        StringRequest request = new StringRequest(Request.Method.PUT,
                Constants.SERVER_URL + Constants.SERVER_PATH_USER ,
                gson.toJson(userDetails), headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        Type type = new TypeToken<ServerResponse
                                <String>>(){}.getType();
                        try {
                            ServerResponse<String> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.isSuccess() && responseListener != null)
                                responseListener.onResponse(null);
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
        VolleySingleton.getInstance(Multicards.getMainActivity()).
                addToRequestQueue(request);
    }

    public static final void newGameRequest(
            String strSetID,
            final Response.Listener<Game> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        Log.d(LOG_TAG, "New game request");
        if (strSetID != null)
            headers.put(Constants.HEADER_SETID, strSetID);
        StringRequest request = new StringRequest(Request.Method.POST,
                Constants.SERVER_URL + Constants.SERVER_PATH_GAME ,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        Type type = new TypeToken<ServerResponse
                                <Game>>(){}.getType();
                        try {
                            ServerResponse<Game> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.isSuccess() && res.data != null
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
        VolleySingleton.getInstance(Multicards.getMainActivity()).
                addToRequestQueue(request);
    }

    public void uploadImageRequest(final String strImageURI) {
        HashMap<String, String> headers = makeHTTPHeaders();
        MultipartRequest uploadRequest = new MultipartRequest(
                Constants.SERVER_URL+Constants.SERVER_PATH_UPLOAD,
                strImageURI,
                headers,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, response.toString());
                        try {
                            Multicards.getPreferences().strAvatar = strImageURI;
                            Multicards.getPreferences().savePreferences();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(LOG_TAG, "Exception " + e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(LOG_TAG, "Error: " + error.getMessage());
            }
        }
        );
        VolleySingleton.getInstance(Multicards.
                getMainActivity()).addToRequestQueue(uploadRequest);
    }

    public static final void getCardsetsRequest(
            final Response.Listener<ArrayList<CardSet>> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        Log.d(LOG_TAG, "Get cardsets request");
        StringRequest request = new StringRequest(Request.Method.GET,
                Constants.SERVER_URL + Constants.SERVER_PATH_CARDSETS ,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        Type type = new TypeToken<ServerResponse
                                <ArrayList<CardSet>>>(){}.getType();
                        try {
                            ServerResponse<ArrayList<CardSet>> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.isSuccess() && (responseListener != null) &&
                                    res.data != null)
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
        VolleySingleton.getInstance(Multicards.getMainActivity()).
                addToRequestQueue(request);
    }

    public static final String searchCardsetQuizletRequest(
            String strKeyWords,
            final Response.Listener<QuizletSearchResult> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        Log.d(LOG_TAG, "Search cardsets request for " + strKeyWords);
        StringRequest request = new StringRequest(Request.Method.GET,
                Constants.QUIZLET_CARDSET_SEARCH_URL + "?client_id=" + ConstantsPrivate.QUIZLET_CLIENT_ID
                + "&q=" + strKeyWords,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        try {
                            QuizletSearchResult res =
                                    gson.fromJson(response, QuizletSearchResult.class);
                            if ( res != null && res.sets != null && (responseListener != null))
                                responseListener.onResponse(res);
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
        String strTag = "search_request_" + strKeyWords;
        request.setTag(strTag);
        VolleySingleton.getInstance(Multicards.getMainActivity()).
                addToRequestQueue(request);
        return strTag;
    }

    public static final void fetchQuizletCardsetRequest(
            String strSetID,
            final Response.Listener<QuizletCardsetDescriptor> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        Log.d(LOG_TAG, "Fetch cardset for " + strSetID);
        StringRequest request = new StringRequest(Request.Method.GET,
                Constants.QUIZLET_CARDSET_URL + strSetID + "?client_id=" +
                        ConstantsPrivate.QUIZLET_CLIENT_ID,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        try {
                            QuizletCardsetDescriptor res =
                                    gson.fromJson(response, QuizletCardsetDescriptor.class);
                            if ( res != null && (responseListener != null))
                                responseListener.onResponse(res);
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
        VolleySingleton.getInstance(Multicards.getMainActivity()).
                addToRequestQueue(request);
    }

    public static void cancelRequestByTag(final String strTag) {
        VolleySingleton.getInstance(Multicards.getMainActivity()).getRequestQueue().
                cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return request.getTag().toString().equals(strTag);
                    }
                });
    }

    public void setSocketIO (Socket socket){
        this.mSocketIO = socket;
    }

    public Socket getSocketIO() {
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
            Multicards.getMainActivity().
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String strMessageType = "";
                    try {
                        strMessageType = new JSONObject(args[0].toString()).
                                getString(Constants.JSON_SOCK_MSG_TYPE);
                    } catch (JSONException e) {
                        Log.d(LOG_TAG, "Json exception while processing " + args[0].toString());
                    }
                    Log.d(LOG_TAG, "Received socket message " + args[0]);
                    if (strMessageType.equals(Constants.SOCK_MSG_TYPE_ANNOUNCE_SOCKETID)) {
                        Type type = new TypeToken<SocketMessage<String>>() {}.getType();
                        SocketMessage<String> socketMessage = gson.fromJson(args[0].toString(), type);
                        String socketID = (String) socketMessage.msg_body;
                        Multicards.getPreferences().strSocketID = socketID;
                        Log.d(LOG_TAG, "Assigned socket ID to " + socketID);
                    } else if (strMessageType.equals(Constants.SOCK_MSG_TYPE_ANNOUNCE_NEW_QUESTION)) {
                        Type type = new TypeToken<SocketMessage<Question>>() {}.getType();
                        SocketMessage<Question> socketMessage = gson.fromJson(args[0].toString(), type);
                        Multicards.getMultiplayerInterface().eventNewQuestion(socketMessage.msg_body);
                    } else if (strMessageType.equals(Constants.SOCK_MSG_TYPE_GAME_START)) {
                        Type type = new TypeToken<SocketMessage<Game>>() {}.getType();
                        SocketMessage<Game> socketMessage = gson.fromJson(args[0].toString(), type);
                        Multicards.getMultiplayerInterface().currentGame =
                                socketMessage.msg_body;
                        Multicards.getMainActivity().showPlayersInfo();
                    } else if (strMessageType.
                            equals(Constants.SOCK_MSG_TYPE_PLAYER_ANSWERED)) {
                        Type type = new TypeToken<SocketMessage<String>>() {}.getType();
                        SocketMessage<String> socketMessage = gson.fromJson(args[0].toString(), type);
                        String strAnswerID = socketMessage.msg_body;
                        Multicards.getMultiplayerInterface().eventOpponentAnswer(strAnswerID);
                    } else if (strMessageType.
                            equals(Constants.SOCK_MSG_TYPE_GAME_END)) {
                        MainMenuFragment mainMenuFragment = new MainMenuFragment();
                        Multicards.getMainActivity().getFragmentManager()
                                .beginTransaction().add(R.id.fragment_flashcard_container,
                                mainMenuFragment).commit();
                        Multicards.getMainActivity().getFragmentManager()
                                .beginTransaction().remove(Multicards.getMainActivity().
                                playersInfoFragment).commit();
                        Multicards.getMainActivity().getFragmentManager()
                                .beginTransaction().remove(Multicards.getMainActivity().
                                currentFlashCardFragment).commit();
                        InputDialogInterface.showGameOverMessage(null);
                        Multicards.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;
                    } else if (strMessageType.
                            equals(Constants.SOCK_MSG_TYPE_ANSWER_ACCEPTED)) {
                        Type type = new TypeToken<SocketMessage<Integer>>() {}.getType();
                        SocketMessage<Integer> socketMessage = gson.fromJson(args[0].toString(), type);
                        int questionID = socketMessage.msg_body;
                        Multicards.getMultiplayerInterface().eventAnswerAccepted(questionID);
                    } else if (strMessageType.
                            equals(Constants.SOCK_MSG_TYPE_ANSWER_REJECTED)) {
                        Type type = new TypeToken<SocketMessage<Integer>>() {}.getType();
                        SocketMessage<Integer> socketMessage = gson.fromJson(args[0].toString(), type);
                        int questionID = socketMessage.msg_body;
                        Multicards.getMultiplayerInterface().eventAnswerRejected(questionID);
                    }
                }
            });
        }
    };

    public static final void socketAnnounceUserID (String strUserID) {
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_ANNOUNCE_USERID;
        msg.msg_body = strUserID;
        Multicards.getServerInterface().getSocketIO().emit("message", gson.toJson(msg));
    }

    private static HashMap<String, String> makeHTTPHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "*/*");
        headers.put(Constants.HEADER_USERID, Multicards.getPreferences().strUserID);
        headers.put(Constants.HEADER_SOCKETID, Multicards.getPreferences().strSocketID);
        Log.d(LOG_TAG, "Setting socket id to " + Multicards.getPreferences().strSocketID);
        return headers;
    }
}
