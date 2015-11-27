package memorizer.freecoders.com.flashcards.json;

import memorizer.freecoders.com.flashcards.common.Constants;

/**
 * Created by alex-mac on 27.11.15.
 */
public class ServerResponse<T> {
    public String result = "";
    public T data = null;
    public Integer code = 0;
    public String timestamp = "0";

    public Boolean isSuccess () {
        return ((result != null) && result.equals(Constants.RESPONSE_RESULT_OK));
    }
}