package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.json.CardSet;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;

/**
 * Created by alex-mac on 13.12.15.
 */
public class CardsetListAdapter extends ArrayAdapter<String> {
    private Context context;
    public static int INT_ITEMS_TYPE_QUIZLET = 0;
    public static int INT_ITEMS_TYPE_CARDSET = 1;
    public int INT_ITEMS_TYPE = INT_ITEMS_TYPE_QUIZLET;
    public ArrayList<QuizletCardsetDescriptor> qvalues = new ArrayList<QuizletCardsetDescriptor>();
    public ArrayList<CardSet> values = new ArrayList<CardSet>();

    public CardsetListAdapter(Context context) {
        super(context, -1);
        this.context = context;
    }

    public void setQValues(ArrayList<QuizletCardsetDescriptor> values) {
        this.qvalues.clear();
        this.qvalues.addAll(values);
        INT_ITEMS_TYPE = INT_ITEMS_TYPE_QUIZLET;
    }

    public void setValues(ArrayList<CardSet> values) {
        this.values.clear();
        this.values.addAll(values);
        INT_ITEMS_TYPE = INT_ITEMS_TYPE_CARDSET;
    }

    @Override
    public int getCount() {
        return (INT_ITEMS_TYPE == INT_ITEMS_TYPE_CARDSET) ? values.size() : qvalues.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        rowView = inflater.inflate(R.layout.cardsetpicker_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewCardsetPickerItem);
        TextView textViewAuthor = (TextView) rowView.findViewById(R.id.textViewCardsetSearchAuthor);

        if (INT_ITEMS_TYPE == INT_ITEMS_TYPE_CARDSET) {
            textView.setText(values.get(position).title);
            textViewAuthor.setText("");
        } else {
            textView.setText(qvalues.get(position).title);
            textViewAuthor.setText(qvalues.get(position).created_by);
        }

        return rowView;
    }
}
