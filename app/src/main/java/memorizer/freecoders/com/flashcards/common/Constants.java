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
    public final static String SERVER_PATH_UPLOAD = "/upload";

    public final static String RESPONSE_RESULT = "result";
    public final static String RESPONSE_RESULT_OK = "OK";
    public final static String RESPONSE_DATA = "data";
    public final static String RESPONSE_CODE = "code";

    public final static String HEADER_USERID = "id";
    public final static String HEADER_SOCKETID = "socketid";

    public final static String KEY_ID = "id";

    public final static String SOCKET_CHANNEL_NAME = "event";

    public static String JSON_SOCK_MSG_TYPE = "msg_type";
    public static String SOCK_MSG_TYPE_ANNOUNCE_SOCKETID = "socket_id_announce";
    public static String SOCK_MSG_TYPE_ANNOUNCE_NEW_QUESTION = "new_question";
    public static String SOCK_MSG_TYPE_PLAYER_ANSWERED = "player_answered";
    public static String SOCK_MSG_TYPE_GAME_START = "game_start";
    public static String SOCK_MSG_TYPE_GAME_END = "game_end";
    public static String SOCK_MSG_TYPE_PLAYER_STATUS_UPDATE = "player_status";

    public static Integer GAME_STATUS_SEARCHING_PLAYERS = 0;
    public static Integer GAME_STATUS_IN_PROGRESS = 1;
    public static Integer GAME_STATUS_COMPLETED = 2;

    public static String PLAYER_STATUS_THINKING = "player_thinking";
    public static String PLAYER_STATUS_ANSWERED = "player_answered";
    public static String PLAYER_STATUS_WAITING = "player_waiting";

    public static Integer UI_STATE_MAIN_MENU = 0;
    public static Integer UI_STATE_TRAIN_MODE = 10;
    public static Integer UI_STATE_MULTIPLAYER_MODE = 20;
}
