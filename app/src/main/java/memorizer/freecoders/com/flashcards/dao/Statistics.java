package memorizer.freecoders.com.flashcards.dao;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by alex-mac on 24.01.16.
 */

@Table(name = "Statistics")
public class Statistics extends Model{
    @Column(name = "gid")
    public String gid;
    @Column(name = "score")
    public Integer score;
    @Column(name = "total_cards_shown")
    public Integer total_cards_shown;
    @Column(name = "total_correct_answers")
    public Integer total_correct_answers;
    @Column(name = "details")
    public String details;
}
