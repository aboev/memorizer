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
    @Column(name = "title")
    public String title;
    @Column(name = "created_by")
    public String created_by;
    @Column(name = "url")
    public String url;
    @Column(name = "terms_count")
    public Integer terms_count;
    @Column(name = "inverted")
    public Boolean inverted;
    @Column(name = "has_images")
    public Boolean has_images;
}
