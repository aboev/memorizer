package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.json.CardSet;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;

/**
 * Created by alex-mac on 13.12.15.
 */
public class CardsetListAdapter extends ArrayAdapter<String> {
    private Context context;
    public ArrayList<QuizletCardsetDescriptor> qvalues = new ArrayList<QuizletCardsetDescriptor>();
    public ArrayList<CardSet> values = null;

    public CardsetListAdapter(Context context) {
        super(context, -1);
        this.context = context;
    }

    public void setQValues(ArrayList<QuizletCardsetDescriptor> values) {
        this.qvalues.clear();
        this.qvalues.addAll(values);
    }

    public void setValues(ArrayList<CardSet> values) {
        if (this.values == null) this.values = new ArrayList<CardSet>();
        this.values.clear();
        this.values.addAll(values);
    }

    @Override
    public int getCount() {
        return (values != null) ? values.size() : qvalues.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        rowView = inflater.inflate(R.layout.cardsetpicker_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewCardsetPickerItem);
        TextView textViewAuthor = (TextView) rowView.findViewById(R.id.textViewCardsetSearchAuthor);

        if (values != null) {
            textView.setText(values.get(position).title);
            textViewAuthor.setText("");
        } else {
            textView.setText(qvalues.get(position).title);
            textViewAuthor.setText(qvalues.get(position).created_by);
        }

        return rowView;
    }
}
