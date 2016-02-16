package memorizer.freecoders.com.flashcards.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.MutableBoolean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

import memorizer.freecoders.com.flashcards.json.CardSet;
import memorizer.freecoders.com.flashcards.json.TagDescriptor;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;

/**
 * Created by alex-mac on 27.11.15.
 */
public final class Preferences {
    private static String LOG_TAG = "Preferences";

    private SharedPreferences settings;
    private Context context;

    Gson gson = new Gson();

    private String KEY_USERID = "userid";
    private String KEY_USERNAME = "username";
    private String KEY_PHONE = "phone";
    private String KEY_EMAIL = "email";
    private String KEY_REGISTER_STATUS = "status";
    private String KEY_FIRST_START = "first_start";
    private String KEY_SOCKET_ID = "socket_id";
    private String KEY_AVATAR = "avatar";
    private String KEY_RECENT_SETS = "recent_sets";
    private String KEY_RECENT_SET_DESCRIPTORS = "recent_set_descriptors";
    private String KEY_RECENT_OPPONENTS = "recent_opponents";
    private String KEY_USER_DETAILS_CACHE = "user_details_cache";
    private String KEY_TAG_DESCRIPTORS = "tag_descriptors";
    private String KEY_USER_SCORE = "user_score";
    private String KEY_SERVER_INFO = "server_info";
    private String KEY_PUSHID = "pushid";
    private String KEY_PUSHID_SAVED = "pushid_saved";

    public String strUserID = "";
    public String strUserName = "";
    public String strPhone = "";
    public String strEmail = "";
    public String strSocketID = "";
    public String strAvatar = "";
    public Integer intRegisterStatus = 0;   // 0 - not registered,
    // 1 - waiting for sms code, 2 registered
    public String strServerInfo = "";
    public String strPushID = "";
    public Integer intLastOpenedTab = 0;
    public Integer intUserScore = 0;
    public Boolean boolFirstStart = true;
    public Boolean boolPushIDsent = false;
    public HashMap<String, Long> recentSets;
    public HashMap<String, QuizletCardsetDescriptor> recentSetDescriptors;
    public HashMap<String, Long> recentOpponents;
    public HashMap<String, UserDetails> userDetailsCache;
    public HashMap<String, TagDescriptor> tagDescriptors;

    public Preferences(Context context) {
        settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        this.context = context;
    }

    public final Boolean loadPreferences(){
        strUserID = settings.getString(KEY_USERID, "");
        strUserName = settings.getString(KEY_USERNAME, "");
        strPhone = settings.getString(KEY_PHONE, "");
        strEmail = settings.getString(KEY_EMAIL, "");
        strSocketID = settings.getString(KEY_SOCKET_ID, "");
        strAvatar = settings.getString(KEY_AVATAR, "");
        intRegisterStatus = settings.getInt(KEY_REGISTER_STATUS, 0);
        intUserScore = settings.getInt(KEY_USER_SCORE, 0);
        strServerInfo = settings.getString(KEY_SERVER_INFO, "{}");
        strPushID = settings.getString(KEY_PUSHID, "");
        boolPushIDsent = settings.getBoolean(KEY_PUSHID_SAVED, false);

        Type type = new TypeToken<HashMap<String, Long>>() {}.getType();
        recentSets = gson.fromJson(settings.getString(KEY_RECENT_SETS, "{}"), type);

        type = new TypeToken<HashMap<String, QuizletCardsetDescriptor>>() {}.getType();
        recentSetDescriptors = gson.fromJson(settings.getString(KEY_RECENT_SET_DESCRIPTORS, "{}"), type);

        type = new TypeToken<HashMap<String, Long>>() {}.getType();
        recentOpponents = gson.fromJson(settings.getString(KEY_RECENT_OPPONENTS, "{}"), type);

        type = new TypeToken<HashMap<String, UserDetails>>() {}.getType();
        userDetailsCache = gson.fromJson(settings.getString(KEY_USER_DETAILS_CACHE, "{}"), type);

        type = new TypeToken<HashMap<String, TagDescriptor>>() {}.getType();
        tagDescriptors = gson.fromJson(settings.getString(KEY_TAG_DESCRIPTORS, "{}"), type);

        return ((strUserID.length() != 0) && (intRegisterStatus == Constants.STATUS_REGISTERED));
    }

    public final void savePreferences(){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_USERID, strUserID);
        editor.putString(KEY_USERNAME, strUserName);
        editor.putString(KEY_PHONE, strPhone);
        editor.putString(KEY_EMAIL, strEmail);
        editor.putString(KEY_AVATAR, strAvatar);
        editor.putInt(KEY_REGISTER_STATUS, intRegisterStatus);
        editor.putInt(KEY_USER_SCORE, intUserScore);
        editor.putBoolean(KEY_FIRST_START, boolFirstStart);
        editor.putString(KEY_RECENT_SETS, gson.toJson(recentSets));
        editor.putString(KEY_RECENT_SET_DESCRIPTORS, gson.toJson(recentSetDescriptors));
        editor.putString(KEY_RECENT_OPPONENTS, gson.toJson(recentOpponents));
        editor.putString(KEY_USER_DETAILS_CACHE, gson.toJson(userDetailsCache));
        editor.putString(KEY_TAG_DESCRIPTORS, gson.toJson(tagDescriptors));
        editor.putString(KEY_SERVER_INFO, strServerInfo);
        editor.putString(KEY_PUSHID, strPushID);
        editor.putBoolean(KEY_PUSHID_SAVED, boolPushIDsent);
        editor.commit();
    }

    public void saveRecentOpponent (UserDetails opponent) {
        Log.d(LOG_TAG, "Saving recent opponent " + gson.toJson(recentOpponents) );
        Log.d(LOG_TAG, "Saving recent opponent " + gson.toJson(userDetailsCache) );
        recentOpponents.put(opponent.id, System.currentTimeMillis());
        userDetailsCache.put(opponent.id, opponent);
        savePreferences();
    }

    public void setRecentCardset(CardSet cardset) {
        Log.d(LOG_TAG, "Setting recent cardset " + cardset.gid);
        QuizletCardsetDescriptor qcardset = new QuizletCardsetDescriptor();
        qcardset.title = cardset.title;
        qcardset.gid = cardset.gid;
        recentSets.put(qcardset.gid, System.currentTimeMillis());
        recentSetDescriptors.put(qcardset.gid, qcardset);
        savePreferences();
    }

    public void setRecentCardset(QuizletCardsetDescriptor cardset, String strGID) {
        Log.d(LOG_TAG, "Setting recent qcardset " + strGID);
        cardset.gid = strGID;
        Multicards.getPreferences().recentSets.put(strGID, System.currentTimeMillis());
        Multicards.getPreferences().recentSetDescriptors.put(strGID, cardset);
        Multicards.getPreferences().savePreferences();
    }

}