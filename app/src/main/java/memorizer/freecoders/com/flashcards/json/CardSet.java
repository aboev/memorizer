package memorizer.freecoders.com.flashcards.json;

/**
 * Created by alex-mac on 12.12.15.
 */
public class CardSet {
    public String gid;
    String cardset_id;
    public String title;
    public String lang_terms;
    public String lang_definitions;
    String[] likes = new String[]{};
    public Integer[] flags = new Integer[]{};
    public Integer like_count;
    public Boolean has_images = false;
}
