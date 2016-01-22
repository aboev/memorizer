package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by alex-mac on 22.01.16.
 */
public class OpponentListAdapter extends ArrayAdapter<String> {
    private Context context;

    public OpponentListAdapter(Context context) {
        super(context, -1);
        this.context = context;
    }
}
