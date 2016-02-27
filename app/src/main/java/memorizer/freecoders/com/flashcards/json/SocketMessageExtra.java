package memorizer.freecoders.com.flashcards.json;

import java.util.ArrayList;

/**
 * Created by alex-mac on 06.01.16.
 */
public class SocketMessageExtra<T, K> {
    public ArrayList<String> id_to = new ArrayList<>();
    public String msg_type = "";
    public T msg_body;
    public K msg_extra;
    public Integer msg_id;
}
