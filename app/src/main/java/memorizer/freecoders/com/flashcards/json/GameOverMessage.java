package memorizer.freecoders.com.flashcards.json;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alex-mac on 12.01.16.
 */
public class GameOverMessage {
    public UserDetails winner;
    public HashMap<String, Integer> scores_before;
    public HashMap<String, Integer> scores;
    public HashMap<String, ArrayList<BonusDescriptor>> bonuses;
}
