package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;

/**
 * Created by alex-mac on 13.12.15.
 */
public class CardsetListAdapter extends ArrayAdapter<String> {
    private Context context;
    public ArrayList<QuizletCardsetDescriptor> values = new ArrayList<QuizletCardsetDescriptor>();

    public CardsetListAdapter(Context context) {
        super(context, -1);
        this.context = context;
    }

    public void setValues(ArrayList<QuizletCardsetDescriptor> values) {
        this.values.clear();
        this.values.addAll(values);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        rowView = inflater.inflate(R.layout.cardsetpicker_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewCardsetPickerItem);

        textView.setText(values.get(position).title + " (" + values.get(position).created_by + " )");

        return rowView;
    }
}
