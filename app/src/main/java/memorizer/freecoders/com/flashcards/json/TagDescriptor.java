package memorizer.freecoders.com.flashcards.json;

import java.util.HashMap;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 16.01.16.
 */
public class TagDescriptor {
    public String tag_id;
    public HashMap<String, String> tag_name;
    public String color;

    public String getName () {
        String strRes = "";
        if (tag_name != null) {
            if (tag_name.containsKey(Utils.getLocale())) strRes =
                    tag_name.get(Utils.getLocale());
            else if (tag_name.containsKey(Constants.DEFAULT_LOCALE)) strRes =
                    tag_name.get(Constants.DEFAULT_LOCALE);
        }
        return strRes;
    }
}
