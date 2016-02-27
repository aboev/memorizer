package memorizer.freecoders.com.flashcards.json;

import java.util.HashMap;

/**
 * Created by alex-mac on 28.11.15.
 */
public class Game {
    public String game_id;
    public String game_gid;
    public HashMap<String, String> players;
    public Integer status;
    public HashMap<String, UserDetails> profiles;
    public CardSet cardset;
}
