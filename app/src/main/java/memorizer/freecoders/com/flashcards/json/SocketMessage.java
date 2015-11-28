package memorizer.freecoders.com.flashcards.json;

import java.util.ArrayList;

/**
 * Created by alex-mac on 28.11.15.
 */
public class SocketMessage<T> {
    public ArrayList<String> id_to = new ArrayList<>();
    public String msg_type = "";
    public T msg_body;
}
