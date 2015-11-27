package memorizer.freecoders.com.flashcards.common;

/**
 * Created by alex-mac on 21.11.15.
 */
public class Constants {
    public final static String LOG_TAG = "com.freecoders.flashcards";
    public static final String PREFS_NAME = "MulticardsPrefs";

    public static final int STATUS_UNREGISTERED = 0;
    public static final int STATUS_SMS_WAIT = 1;
    public static final int STATUS_REGISTERED = 2;

    public final static String SERVER_URL = "http://multicards.snufan.com:80";
    public final static String SOCKET_SERVER_URL = "http://multicards.snufan.com:5001";
    public final static String SERVER_PATH_USER = "/user";
    public final static String SERVER_PATH_GAME = "/game";

    public final static String RESPONSE_RESULT = "result";
    public final static String RESPONSE_RESULT_OK = "OK";
    public final static String RESPONSE_DATA = "data";
    public final static String RESPONSE_CODE = "code";

    public final static String HEADER_USERID = "id";

    public final static String KEY_ID = "id";

    public final static String SOCKET_CHANNEL_NAME = "event";


}
