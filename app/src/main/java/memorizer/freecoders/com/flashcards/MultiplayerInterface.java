package memorizer.freecoders.com.flashcards;

/**
 * Created by alex-mac on 21.11.15.
 */
public class MultiplayerInterface {
    public static int EVENT_INCOMING_INVITATION = 0;
    public static int EVENT_INVITATION_ACCEPTED = 10;
    public static int EVENT_NEW_QUESTION = 20;
    public static int EVENT_USER_ANSWER = 30;
    public static int EVENT_OPPONENT_ANSWER = 40;
    public static int EVENT_START_SESSION = 50;
    public static int EVENT_FINISH_SESSION = 60;

    public void renderEvent (int intEventType, String strData) {

    }
}
