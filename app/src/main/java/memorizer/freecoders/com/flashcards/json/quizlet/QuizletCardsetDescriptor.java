package memorizer.freecoders.com.flashcards.json.quizlet;

import java.util.ArrayList;

/**
 * Created by alex-mac on 13.12.15.
 */
public class QuizletCardsetDescriptor {
    public String gid;
    public Long id;
    public String url;
    public String title;
    public String created_by;
    public Boolean has_images;
    public ArrayList<QuizletTermDescriptor> terms;
}
