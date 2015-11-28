package memorizer.freecoders.com.flashcards.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by alex-mac on 27.11.15.
 */
public final class Preferences {
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

    public String strUserID = "";
    public String strUserName = "";
    public String strPhone = "";
    public String strEmail = "";
    public String strSocketID = "";
    public Integer intRegisterStatus = 0;   // 0 - not registered,
    // 1 - waiting for sms code, 2 registered
    public Integer intLastOpenedTab = 0;
    public Boolean boolFirstStart = true;

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
        intRegisterStatus = settings.getInt(KEY_REGISTER_STATUS, 0);
        return ((strUserID.length() != 0) && (intRegisterStatus == Constants.STATUS_REGISTERED));
    }

    public final void savePreferences(){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_USERID, strUserID);
        editor.putString(KEY_USERNAME, strUserName);
        editor.putString(KEY_PHONE, strPhone);
        editor.putString(KEY_EMAIL, strEmail);
        editor.putInt(KEY_REGISTER_STATUS, intRegisterStatus);
        editor.putBoolean(KEY_FIRST_START, boolFirstStart);
        editor.commit();
    }

}