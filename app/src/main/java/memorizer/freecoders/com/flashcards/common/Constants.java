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
    public final static String SERVER_PATH_USERS = "/users";
    public final static String SERVER_PATH_GAME = "/game";
    public final static String SERVER_PATH_GAME_NEW = "/game/new";
    public final static String SERVER_PATH_UPLOAD = "/image";
    public final static String SERVER_PATH_CARDSETS = "/cardsets";
    public final static String SERVER_PATH_POPULAR = "/popular";
    public final static String SERVER_PATH_SEARCH = "/search";
    public final static String SERVER_PATH_LIKE = "/like";
    public final static String SERVER_PATH_TAGS = "/tags";
    public final static String SERVER_PATH_TAG = "/tag";
    public final static String SERVER_PATH_UNTAG = "/untag";
    public final static String SERVER_PATH_INFO = "/info";

    public final static String RESPONSE_RESULT = "result";
    public final static String RESPONSE_RESULT_OK = "OK";
    public final static String RESPONSE_DATA = "data";
    public final static String RESPONSE_CODE = "code";
    public final static Integer ERROR_USER_NOT_FOUND = 105;
    public final static Integer ERROR_GAME_NOT_FOUND = 106;

    public final static String HEADER_USERID = "id";
    public final static String HEADER_USERNAME = "name";
    public final static String HEADER_SOCKETID = "socketid";
    public final static String HEADER_SETID = "setid";
    public final static String HEADER_OPPONENTNAME = "opponentname";
    public final static String HEADER_TAGID = "tagid";
    public final static String HEADER_IDS = "ids";
    public final static String HEADER_MULTIPLAYER_TYPE = "multiplayerType";
    public final static String HEADER_GAMEID = "gameid";

    public final static String KEY_ID = "id";
    public final static String KEY_LATEST_APK_VER = "latest_ver";
    public final static String KEY_LATEST_APK_URL = "latest_apk";
    public final static String KEY_MIN_CLIENT_VERSION = "min_client";

    public final static String SOCKET_CHANNEL_NAME = "event";

    public static String JSON_SOCK_MSG_TYPE = "msg_type";
    public static String SOCK_MSG_TYPE_ANNOUNCE_SOCKETID = "socket_id_announce";
    public static String SOCK_MSG_TYPE_ANNOUNCE_NEW_QUESTION = "new_question";
    public static String SOCK_MSG_TYPE_PLAYER_ANSWERED = "player_answered";
    public static String SOCK_MSG_TYPE_GAME_START = "game_start";
    public static String SOCK_MSG_TYPE_GAME_END = "game_end";
    public static String SOCK_MSG_TYPE_GAME_STOP = "game_stop";
    public static String SOCK_MSG_TYPE_QUIT_GAME = "quit_game";
    public static String SOCK_MSG_TYPE_PLAYER_STATUS_UPDATE = "player_status";
    public static String SOCK_MSG_TYPE_ANNOUNCE_USERID = "announce_userid";
    public static String SOCK_MSG_TYPE_ANSWER_ACCEPTED = "answer_accepted";
    public static String SOCK_MSG_TYPE_ANSWER_REJECTED = "answer_rejected";
    public static String SOCK_MSG_TYPE_CHECK_NAME = "check_name";
    public static String SOCK_MSG_TYPE_CHECK_NETWORK = "check_network";
    public static String SOCK_MSG_TYPE_GAME_INVITE = "game_invite";
    public static String SOCK_MSG_TYPE_INVITE_ACCEPTED = "invite_accepted";
    public static String SOCK_MSG_TYPE_CONFIRM = "receive_confirm";

    public static Integer GAME_STATUS_SEARCHING_PLAYERS = 0;
    public static Integer GAME_STATUS_WAITING_OPPONENT = 1;
    public static Integer GAME_STATUS_IN_PROGRESS = 2;
    public static Integer GAME_STATUS_COMPLETED = 3;

    public static String PLAYER_STATUS_THINKING = "player_thinking";
    public static String PLAYER_STATUS_ANSWERED = "player_answered";
    public static String PLAYER_STATUS_WAITING = "player_waiting";

    public static Integer UI_STATE_MAIN_MENU = 0;
    public static Integer UI_STATE_TRAIN_MODE = 10;
    public static Integer UI_STATE_MULTIPLAYER_MODE = 20;
    public static Integer UI_STATE_CARD_PICK = 30;
    public static Integer UI_STATE_GAME_OVER = 40;
    public static Integer UI_STATE_SETTINGS = 50;

    public static Integer UI_DIALOG_WAITING_OPPONENT = 60;
    public static Integer UI_DIALOG_PICK_OPPONENT = 70;
    public static Integer UI_DIALOG_INCOMING_INVITATION = 80;

    public static Integer GAMEPLAY_QUESTIONS_PER_GAME = 25;
    public static Integer GAMEPLAY_OPTIONS_PER_QUESTION = 4;

    public final static String QUIZLET_CARDSET_SEARCH_URL = "https://api.quizlet.com/2.0/search/sets";
    public final static String QUIZLET_CARDSET_URL = "https://api.quizlet.com/2.0/sets/";

    public final static String INTENT_META_NEXT_FRAGMENT = "nextFragment";
    public final static String INTENT_META_SET_ID = "setID";
    public final static String INTENT_META_GAME_TYPE = "gameType";
    public final static String INTENT_META_GAMEOVER_MESSAGE = "gameOverMessage";

    public final static int ANIMATION_SLIDE = 0;
    public final static int ANIMATION_FLIP = 1;

    public final static int INTENT_PICK_IMAGE = 111;
    public final static int INTENT_PICK_IMAGE_KITKAT = 112;
    public final static String FILENAME_AVATAR = "avatar.jpg";

    public final static String DEFAULT_LOCALE = "en";

    public final static String TAG_PICK_OPPONENT_FRAGMENT = "pick_opponent_dialog";
    public final static String TAG_PICK_GAME_FRAGMENT = "pick_game_dialog";
    public final static String TAG_INVITATION_FRAGMENT = "invitation_dialog";

    public final static String APP_FOLDER = "multicards";

    public static Integer FLAG_CARDSET_INVERTED = 0;

}
