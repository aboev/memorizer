package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import memorizer.freecoders.com.flashcards.R;

/**
 * Created by alex-mac on 07.11.15.
 */
public class ListViewAdapter extends ArrayAdapter<String>{
    private Context context;
    private ArrayList<String> values;
    private int intCorrectAnswer = -1;
    private Set<Integer> setWrongAnswers = new HashSet<Integer>();

    public ListViewAdapter(Context context) {
        super(context, -1);
        this.context = context;
    }

    public void setCorrectAnswer (int intCorrectAnswer){
        this.intCorrectAnswer = intCorrectAnswer;
    }

    public void setWrongAnswer (int intWrongAnswer){
        if (intWrongAnswer == -1)
            setWrongAnswers.clear();
        else
            setWrongAnswers.add(intWrongAnswer);
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public String getItem(int position) {
        return values.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if (position == intCorrectAnswer)
            rowView = inflater.inflate(R.layout.button_right, parent, false);
        else if (setWrongAnswers.contains(position))
            rowView = inflater.inflate(R.layout.button_wrong, parent, false);
        else
            rowView = inflater.inflate(R.layout.button, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.TextView_ButtonName);
        if (values != null) textView.setText(values.get(position));
        return rowView;
    }
}
