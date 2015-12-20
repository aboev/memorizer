package memorizer.freecoders.com.flashcards.json.quizlet;

import java.util.ArrayList;

/**
 * Created by alex-mac on 13.12.15.
 */
public class QuizletSearchResult {
    public int total_results;
    public int total_pages;
    public int page;
    public int image_set_count;
    public ArrayList<QuizletCardsetDescriptor> sets;
}
