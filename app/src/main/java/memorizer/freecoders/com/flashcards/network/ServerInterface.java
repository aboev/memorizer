package memorizer.freecoders.com.flashcards.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.ConstantsPrivate;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.CardSet;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.ServerResponse;
import memorizer.freecoders.com.flashcards.json.TagDescriptor;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletSearchResult;

/**
 * Created by alex-mac on 21.11.15.
 */
public class ServerInterface {

    private static Gson gson = new Gson();

    private static String LOG_TAG = "ServerInterface";

    public static final void registerUserRequest(
            UserDetails userDetails,
            final Response.Listener<UserDetails> responseListener,
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
                                <UserDetails>>(){}.getType();
                        try {
                            ServerResponse<UserDetails> res =
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
                                <UserDetails>>(){}.getType();
                        try {
                            ServerResponse<UserDetails> res =
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
            String strOpponentName,
            final Response.Listener<Game> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        headers.put(Constants.HEADER_OPPONENTNAME, strOpponentName);
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

    public static final void getPopularCardsetsRequest(
            final Response.Listener<ArrayList<CardSet>> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        Log.d(LOG_TAG, "Get popular cardsets request");
        StringRequest request = new StringRequest(Request.Method.GET,
                Constants.SERVER_URL + Constants.SERVER_PATH_POPULAR,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        try {
                            Type type = new TypeToken<ServerResponse
                                    <ArrayList<CardSet>>>(){}.getType();
                            ServerResponse<ArrayList<CardSet>> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.data != null && (responseListener != null))
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

    public static final void likeCardsetRequest(
            String strSetID,
            final Response.Listener<Boolean> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        headers.put(Constants.HEADER_SETID, strSetID);
        Log.d(LOG_TAG, "Like cardset request");
        StringRequest request = new StringRequest(Request.Method.POST,
                Constants.SERVER_URL + Constants.SERVER_PATH_LIKE,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        try {
                            Type type = new TypeToken<ServerResponse
                                    <String>>(){}.getType();
                            ServerResponse<String> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.isSuccess() && (responseListener != null))
                                responseListener.onResponse(true);
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

    public static final void getTagsRequest(
            final Response.Listener<ArrayList<TagDescriptor>> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        Log.d(LOG_TAG, "Get tags request");
        StringRequest request = new StringRequest(Request.Method.GET,
                Constants.SERVER_URL + Constants.SERVER_PATH_TAGS ,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        Type type = new TypeToken<ServerResponse
                                <ArrayList<TagDescriptor>>>(){}.getType();
                        try {
                            ServerResponse<ArrayList<TagDescriptor>> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.isSuccess() && (responseListener != null) &&
                                    res.data != null) {
                                responseListener.onResponse(res.data);
                                for (int i = 0; i < res.data.size(); i++) {
                                    Multicards.getPreferences().tagDescriptors.
                                            put(res.data.get(i).tag_id, res.data.get(i));
                                }
                                Multicards.getPreferences().savePreferences();
                            } else if (errorListener != null)
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
                error.printStackTrace();
                Log.d(LOG_TAG, "error " + error.getLocalizedMessage());
                if (errorListener != null) errorListener.onErrorResponse(error);
            }
        }
        );
        VolleySingleton.getInstance(Multicards.getMainActivity()).
                addToRequestQueue(request);
    }

    public static final void tagCardsetRequest(
            String strGID,
            ArrayList<String> tagIDS,
            final Response.Listener<ArrayList<TagDescriptor>> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        String strTags = tagIDS.get(0);
        for (int i = 1; i < tagIDS.size(); i++) strTags = strTags + "," + tagIDS.get(i);
        headers.put(Constants.HEADER_SETID, strGID);
        headers.put(Constants.HEADER_TAGID, strTags);

        Log.d(LOG_TAG, "Post tag request");
        StringRequest request = new StringRequest(Request.Method.POST,
                Constants.SERVER_URL + Constants.SERVER_PATH_TAG ,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        Type type = new TypeToken<ServerResponse
                                <String>>(){}.getType();
                        try {
                            ServerResponse<String> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.isSuccess() && (responseListener != null))
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

    public static final void untagCardsetRequest(
            String strGID,
            ArrayList<String> tagIDS,
            final Response.Listener<ArrayList<TagDescriptor>> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();
        String strTags = tagIDS.get(0);
        for (int i = 1; i < tagIDS.size(); i++) strTags = strTags + "," + tagIDS.get(i);
        headers.put(Constants.HEADER_SETID, strGID);
        headers.put(Constants.HEADER_TAGID, strTags);

        Log.d(LOG_TAG, "Drop tag request");
        StringRequest request = new StringRequest(Request.Method.POST,
                Constants.SERVER_URL + Constants.SERVER_PATH_UNTAG ,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        Type type = new TypeToken<ServerResponse
                                <String>>(){}.getType();
                        try {
                            ServerResponse<String> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.isSuccess() && (responseListener != null))
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

    public static final void searchCardsetsRequest(
            ArrayList<String> tagIDS,
            final Response.Listener<ArrayList<CardSet>> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();

        String strTags = tagIDS.get(0);
        for (int i = 1; i < tagIDS.size(); i++) strTags = strTags + "," + tagIDS.get(i);
        headers.put(Constants.HEADER_TAGID, strTags);

        Log.d(LOG_TAG, "Search cardsets request");
        StringRequest request = new StringRequest(Request.Method.GET,
                Constants.SERVER_URL + Constants.SERVER_PATH_SEARCH,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        try {
                            Type type = new TypeToken<ServerResponse
                                    <ArrayList<CardSet>>>(){}.getType();
                            ServerResponse<ArrayList<CardSet>> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.data != null && (responseListener != null))
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

    public static final void getUserProfiles(
            ArrayList<String> userIDList,
            final Response.Listener<ArrayList<UserDetails>> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();

        String strList = userIDList.get(0);
        for (int i = 1; i < userIDList.size(); i++)
            strList = strList + "," + userIDList.get(i);
        headers.put(Constants.HEADER_IDS, strList);

        Log.d(LOG_TAG, "Get user details for " + strList);
        StringRequest request = new StringRequest(Request.Method.GET,
                Constants.SERVER_URL + Constants.SERVER_PATH_USERS,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        try {
                            Type type = new TypeToken<ServerResponse
                                    <ArrayList<UserDetails>>>(){}.getType();
                            ServerResponse<ArrayList<UserDetails>> res =
                                    gson.fromJson(response, type);
                            if ( res != null && res.data != null && (responseListener != null))
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

    public static final void getUserProfileByName(
            String strName,
            final Response.Listener<UserDetails> responseListener,
            final Response.ErrorListener errorListener) {
        HashMap<String, String> headers = makeHTTPHeaders();

        headers.put(Constants.HEADER_USERNAME, strName);

        Log.d(LOG_TAG, "Get user details by name for " + strName);
        StringRequest request = new StringRequest(Request.Method.GET,
                Constants.SERVER_URL + Constants.SERVER_PATH_USER,
                "", headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, "Response: " + response);
                        try {
                            Type type = new TypeToken<ServerResponse<UserDetails>>(){}.getType();
                            ServerResponse<UserDetails> res = gson.fromJson(response, type);
                            if ( res != null && res.data != null && (responseListener != null))
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

    public static void cancelRequestByTag(final String strTag) {
        VolleySingleton.getInstance(Multicards.getMainActivity()).getRequestQueue().
                cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return request.getTag().toString().equals(strTag);
                    }
                });
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
