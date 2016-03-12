package memorizer.freecoders.com.flashcards.json;

import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 12.03.16.
 */
public class MetaItem {
    public String text;
    public Image image;

    public MetaItem(String strText, Image image) {
        this.text = strText;
        this.image = image;
    }

    public final static MetaItem fromJsonString(String strJson) {
        return Utils.gson.fromJson(strJson, MetaItem.class);
    }
}
