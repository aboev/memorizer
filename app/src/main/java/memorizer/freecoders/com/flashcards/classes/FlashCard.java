package memorizer.freecoders.com.flashcards.classes;

import java.util.ArrayList;

/**
 * Created by alex-mac on 03.11.15.
 */
public class FlashCard {
    public String question;
    public ArrayList<String> options = new ArrayList<String>();
    public int answer_id;
    public int wrong_answer_id;
}
