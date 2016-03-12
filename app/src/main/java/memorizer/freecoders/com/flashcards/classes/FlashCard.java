package memorizer.freecoders.com.flashcards.classes;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.json.MetaItem;

/**
 * Created by alex-mac on 03.11.15.
 */
public class FlashCard {
    public MetaItem question_img;
    public ArrayList<MetaItem> options_img = new ArrayList<MetaItem>();
    public int answer_id;
    public int wrong_answer_id;
}
