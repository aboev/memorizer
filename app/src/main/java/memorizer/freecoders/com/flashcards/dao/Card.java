package memorizer.freecoders.com.flashcards.dao;

/**
 * Created by alex-mac on 01.11.15.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import memorizer.freecoders.com.flashcards.json.Image;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletImageDescriptor;

@Table(name = "Cards")
public class Card extends Model {
    @Column(name = "Question")
    public String question;

    @Column(name = "Answer")
    public String answer;

    @Column(name = "SetID")
    public Long setID;

    @Column(name = "Image")
    public String imageURL;

    @Column(name = "ImageWidth")
    public Integer imageWidth;

    @Column(name = "ImageHeight")
    public Integer imageHeight;

    @Column(name = "First")
    public Boolean first;

    public Image getImageDescriptor () {
        if (hasImage()) {
            Image image = new Image();
            image.url = imageURL;
            image.width = imageWidth;
            image.height = imageHeight;
            return image;
        } else
            return null;
    }

    public Boolean hasImage () {
        return ((imageURL != null) && (!imageURL.isEmpty()) && (imageWidth != null) &&
                (imageWidth > 0) && (imageHeight != null) && (imageHeight > 0));
    }
}
