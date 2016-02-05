package memorizer.freecoders.com.flashcards.dao;

/**
 * Created by alex-mac on 01.11.15.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Cards")
public class Card extends Model {
    @Column(name = "Question")
    public String question;

    @Column(name = "Answer")
    public String answer;

    @Column(name = "SetID")
    public Long setID;

    @Column(name = "First")
    public Boolean first;
}
