package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.MetaItem;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 07.11.15.
 */
public class ListViewAdapter extends ArrayAdapter<MetaItem>{
    private Context context;
    private ArrayList<MetaItem> options;
    private int intCorrectAnswer = -1;
    private Set<Integer> setWrongAnswers = new HashSet<Integer>();
    private Gson gson = new Gson();

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

    public void setValues(ArrayList<MetaItem> imageValues) {
        options = imageValues;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public MetaItem getItem(int position) {
        return options.get(position);
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
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewOption);

        if (options.get(position) != null) {
            if (options.get(position).text != null)
                textView.setText(options.get(position).text);
            if ((options.get(position).image != null) &&
                    (options.get(position).image.url != null) &&
                    (options.get(position).image.width != null) &&
                    (options.get(position).image.height != null)) {
                ArrayList<Integer> size = Utils.scaleToMaxXY(options.get(position).image.width,
                        options.get(position).image.height);
                Integer width = size.get(0);
                Integer height = size.get(1);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = width;
                params.height = height;
                imageView.setLayoutParams(params);
                Multicards.getAvatarLoader().get(options.get(position).image.url,
                        new Utils.AvatarListener(imageView));
                imageView.setVisibility(View.VISIBLE);
            } else
                imageView.setVisibility(View.GONE);
        }

        return rowView;
    }
}
