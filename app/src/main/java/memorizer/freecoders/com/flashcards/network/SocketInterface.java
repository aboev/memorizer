package memorizer.freecoders.com.flashcards.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.GameOverMessage;
import memorizer.freecoders.com.flashcards.json.InvitationDescriptor;
import memorizer.freecoders.com.flashcards.json.Question;
import memorizer.freecoders.com.flashcards.json.SocketMessage;
import memorizer.freecoders.com.flashcards.json.SocketMessageExtra;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 27.12.15.
 */
public class SocketInterface {

    private static Gson gson = new Gson();

    private static String LOG_TAG = "SocketInterface";

    private static Socket mSocketIO;
    private static String mSocketID;

    private static int msg_id_counter = 0;

    private static HashMap<Integer, CallbackInterface> syncCallbacks = new
            HashMap<Integer, CallbackInterface>();

    public final static void setSocketIO (Socket socket){
        mSocketIO = socket;
    }

    public final static Socket getSocketIO() {
        return mSocketIO;
    }

    public final static void setSocketID (String strSocketID) {
        mSocketID = strSocketID;
    }

    public final static String getSocketID () {
        return mSocketID;
    }

    public final static Emitter.Listener onNewSocketMessage = new Emitter.Listener() {
        @Override
        public void call(final Object[] args) {
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
                        if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_ANNOUNCE_NEW_QUESTION)) {
                            Type type = new TypeToken<SocketMessageExtra<Question,
                                    HashMap<String, Integer>> >() {}.getType();
                            SocketMessageExtra<Question, HashMap<String, Integer>> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            Question question = socketMessage.msg_body;
                            HashMap<String, Integer> scores = socketMessage.msg_extra;
                            msgNewQuestion(question, scores);
                        } else if (strMessageType.equals(Constants.SOCK_MSG_TYPE_GAME_START)) {
                            if (FragmentManager.mainMenuFragment.isAdded())
                                Multicards.getMainActivity().getFragmentManager().
                                        beginTransaction().remove(FragmentManager.
                                        mainMenuFragment).commit();
                            Type type = new TypeToken<SocketMessage<Game>>() {}.getType();
                            SocketMessage<Game> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            Game game = socketMessage.msg_body;
                            msgGameStart(game);
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_PLAYER_ANSWERED)) {
                            Type type = new TypeToken<SocketMessage<String>>() {}.getType();
                            SocketMessage<String> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            String strAnswerID = socketMessage.msg_body;
                            msgPlayerAnswered(strAnswerID);
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_GAME_END)) {
                            Type type = new TypeToken<SocketMessage<GameOverMessage>>() {}.getType();
                            SocketMessage<GameOverMessage> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            msgGameEnd(socketMessage.msg_body);
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_GAME_STOP)) {
                            msgGameStop();
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_ANSWER_ACCEPTED)) {
                            Type type = new TypeToken<SocketMessage<Integer>>() {}.getType();
                            SocketMessage<Integer> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            int questionID = socketMessage.msg_body;
                            msgAnswerAccepted(questionID);
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_ANSWER_REJECTED)) {
                            Type type = new TypeToken<SocketMessage<Integer>>() {}.getType();
                            SocketMessage<Integer> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            int questionID = socketMessage.msg_body;
                            msgAnswerRejected(questionID);
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_CHECK_NAME)) {
                            Type type = new TypeToken<
                                    SocketMessage<HashMap<String, Boolean>>>() {}.getType();
                            SocketMessage<HashMap<String, Boolean>> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            HashMap<String, Boolean> nameMap = socketMessage.msg_body;
                            msgCheckName(nameMap);
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_CHECK_NETWORK)) {
                            Type type = new TypeToken<
                                    SocketMessage<Integer>>() {}.getType();
                            SocketMessage<Integer> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            Integer intValue = socketMessage.msg_body;
                            msgCheckNetwork(intValue);
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_GAME_INVITE)) {
                            Type type = new TypeToken<
                                    SocketMessage<InvitationDescriptor>>() {}.getType();
                            SocketMessage<InvitationDescriptor> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            msgGameInvitation(socketMessage.msg_body);
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_INVITE_ACCEPTED)) {
                            Type type = new TypeToken<
                                    SocketMessage<Integer>>() {}.getType();
                            SocketMessage<Integer> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            msgInvitationAccepted(socketMessage.msg_body);
                        }  else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_INVITE_REJECTED)) {
                            Type type = new TypeToken<
                                    SocketMessage<Integer>>() {}.getType();
                            SocketMessage<Integer> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            msgInvitationRejected(socketMessage.msg_body);
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_CONFIRM)) {
                            Type type = new TypeToken<
                                    SocketMessage<Integer>>() {}.getType();
                            SocketMessage<Integer> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            Integer msg_id = socketMessage.msg_body;
                            if (syncCallbacks.containsKey(msg_id)) {
                                syncCallbacks.get(msg_id).onResponse(null);
                                syncCallbacks.remove(msg_id);
                            }
                        } else if (strMessageType.
                                equals(Constants.SOCK_MSG_TYPE_PLAYER_STATUS_UPDATE)) {
                            Type type = new TypeToken<
                                    SocketMessage<String>>() {}.getType();
                            SocketMessage<String> socketMessage =
                                    gson.fromJson(args[0].toString(), type);
                            String status = socketMessage.msg_body;
                            msgStatusUpdate(status);
                        }
                    }
                });
        }
    };

    public final static Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object[] args) {
            Multicards.getPreferences().strSocketID = mSocketIO.id();
            Log.d(LOG_TAG, "Connected to socket, session id = " + mSocketIO.id());
        }
    };

    public static final void socketAnnounceUserID (String strUserID) {
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_ANNOUNCE_USERID;
        msg.msg_body = strUserID;
        mSocketIO.emit("message", gson.toJson(msg));
    }

    public static final void socketCheckName (String strName) {
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_CHECK_NAME;
        msg.msg_body = strName;
        mSocketIO.emit("message", gson.toJson(msg));
    }

    private static void msgAnnounceSocketID (String strSocketID){
        Multicards.getPreferences().strSocketID = strSocketID;
        Log.d(LOG_TAG, "Assigned socket ID to " + strSocketID);
    }

    private static void msgNewQuestion (Question question, HashMap<String, Integer> scores) {
        GameplayManager.newServerQuestion(question, scores);
    }

    private static void msgGameStart (Game game) {
        GameplayManager.startMultiplayerGame(game);
    }

    private static void msgPlayerAnswered (String strAnswerID) {
        Multicards.getMultiplayerInterface().eventOpponentAnswer(strAnswerID);
    }

    private static void msgGameEnd (GameOverMessage gameOverMessage) {
        GameplayManager.quitMultilayerGame(gameOverMessage);
    }

    private static void msgGameStop () {
        GameplayManager.stopMultilayerGame(false);
    }

    private static void msgAnswerAccepted (int intQuestionID) {
        Multicards.getMultiplayerInterface().eventAnswerAccepted(intQuestionID);
    }

    private static void msgAnswerRejected (int intQuestionID) {
        Multicards.getMultiplayerInterface().eventAnswerRejected(intQuestionID);
    }

    private static void msgCheckName (HashMap<String, Boolean> nameMap) {
        if (FragmentManager.userProfileFragment != null)
            FragmentManager.userProfileFragment.nameStatus(nameMap);
    }

    private static void msgCheckNetwork (int value) {
        GameplayManager.networkLatencyCallback(value);
    }

    private static void msgGameInvitation (InvitationDescriptor invitation) {
        GameplayManager.gameInvitation(invitation);
    }

    private static void msgInvitationAccepted (Integer intGameID) {
        GameplayManager.invitationAccepted(intGameID);
    }

    private static void msgInvitationRejected (Integer intGameID) {
        GameplayManager.invitationRejected(intGameID);
    }

    private static void msgStatusUpdate (String status) {
        GameplayManager.statusUpdated(status);
    }

    //====================================================================================

    public static void emitQuitGame () {
        Log.d(LOG_TAG, "emitQuitGame");
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_QUIT_GAME;
        msg.msg_body = "";
        mSocketIO.emit("message", gson.toJson(msg));
    }

    public static void emitStatusUpdate (String strStatus) {
        Log.d(LOG_TAG, "emitStatusUpdate " + strStatus);
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_STATUS_UPDATE;
        msg.msg_body = strStatus;
        SocketInterface.getSocketIO().emit("message", gson.toJson(msg));
    }

    public static void emitPlayerAnswered (String strAnswer) {
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_PLAYER_ANSWERED;
        msg.msg_body = strAnswer;
        msg.id_to = new ArrayList();
        SocketInterface.getSocketIO().emit("message", gson.toJson(msg));
    }

    public static void emitCheckNetwork (int value) {
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_CHECK_NETWORK;
        msg.msg_body = value;
        mSocketIO.emit("message", gson.toJson(msg));
    }

    public static void emitInviteOpponent (String strOpponentName, String strGameID) {
        SocketMessageExtra msg = new SocketMessageExtra();
        msg.msg_type = Constants.SOCK_MSG_TYPE_GAME_INVITE;
        msg.msg_body = strOpponentName;
        msg.msg_extra = strGameID;
        mSocketIO.emit("message", gson.toJson(msg));
    }

    public static void emitInvitationAccepted (int game_id, CallbackInterface callback,
            InvitationDescriptor invitation) {
        Log.d(LOG_TAG, "emitInvitationAccepted " + gson.toJson(invitation));
        SocketMessageExtra msg = new SocketMessageExtra();
        msg.msg_type = Constants.SOCK_MSG_TYPE_INVITE_ACCEPTED;
        msg.msg_body = game_id;
        if (invitation != null)
            msg.msg_extra = gson.toJson(invitation);
        msg.msg_id = msg_id_counter;
        mSocketIO.emit("message", gson.toJson(msg));
        syncCallbacks.put(msg_id_counter, callback);
        msg_id_counter++;
    }

    public static void emitInvitationRejected (int game_id) {
        Log.d(LOG_TAG, "emitInvitationRejected " + game_id);
        SocketMessage msg = new SocketMessage();
        msg.msg_type = Constants.SOCK_MSG_TYPE_INVITE_REJECTED;
        msg.msg_body = game_id;
        mSocketIO.emit("message", gson.toJson(msg));
    }
}
