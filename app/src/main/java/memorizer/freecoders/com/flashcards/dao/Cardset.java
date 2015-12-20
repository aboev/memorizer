package memorizer.freecoders.com.flashcards.dao;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by alex-mac on 16.12.15.
 */

@Table(name = "Cardset")
public class Cardset extends Model {
    @Column(name = "gid")
    public String gid;
    @Column(name = "details")
    public String details;
}
