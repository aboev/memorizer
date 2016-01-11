package memorizer.freecoders.com.flashcards.utils;

import android.content.Intent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.StringRequest;

/**
 * Created by alex-mac on 06.01.16.
 */
public class Utils {
    public static final UserDetails extractOpponentProfile(HashMap<String, UserDetails> map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String strSocketID = (String) pair.getKey();
            UserDetails userDetails = (UserDetails) pair.getValue();
            if ((!strSocketID.equals(Multicards.getPreferences().strSocketID)) &&
                    (userDetails.name != null) && (!userDetails.name.isEmpty()))
                return userDetails;
            it.remove();
        }
        return null;
    }

    public static final Integer extractOpponentScore(HashMap<String, Integer> map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String strSocketID = (String) pair.getKey();
            Integer userScore = (Integer) pair.getValue();
            if ((!strSocketID.equals(Multicards.getPreferences().strSocketID)))
                return userScore;
            it.remove();
        }
        return null;
    }

}
