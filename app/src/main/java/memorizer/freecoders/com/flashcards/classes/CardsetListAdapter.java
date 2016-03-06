package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.CardSet;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;
import memorizer.freecoders.com.flashcards.utils.Utils;

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

        rowView = inflater.inflate(R.layout.item_cardsetpicker, parent, false);

        TextView textViewCardsetTitle = (TextView) rowView.findViewById(R.id.textViewCardsetPickerItem);
        TextView textViewLikeCount = (TextView) rowView.findViewById(R.id.textViewCardsetSearchLikes);
        TextView textViewAuthor = (TextView) rowView.findViewById(R.id.textViewAuthor);
        TextView textViewDate = (TextView) rowView.findViewById(R.id.textViewDate);

        ImageView imageViewLangFrom = (ImageView) rowView.findViewById(R.id.imageViewLangFrom);
        ImageView imageViewLangTo = (ImageView) rowView.findViewById(R.id.imageViewLangTo);
        ImageView imageViewLikes = (ImageView) rowView.findViewById(R.id.imageViewLike);

        Integer intLikeCount = 0;
        String strLangFrom = "";
        String strLangTo = "";
        String strAuthor = "";
        String strCardsetTitle = "";
        String strGID = "";
        String strDate = "";
        if (INT_ITEMS_TYPE == INT_ITEMS_TYPE_CARDSET) {
            if (!Utils.arrayContains(values.get(position).flags, Constants.FLAG_CARDSET_INVERTED)) {
                strLangFrom = values.get(position).lang_terms;
                strLangTo = values.get(position).lang_definitions;
            }else{
                strLangFrom = values.get(position).lang_definitions;
                strLangTo = values.get(position).lang_terms;
            }
            intLikeCount = values.get(position).like_count;
            strCardsetTitle = values.get(position).title;
            strGID = values.get(position).gid;
        } else {
            strLangFrom = qvalues.get(position).lang_terms;
            strLangTo = qvalues.get(position).lang_definitions;
            strAuthor = qvalues.get(position).created_by;
            strCardsetTitle = qvalues.get(position).title;
            strGID = "quizlet_" + qvalues.get(position).id;
        }

        if ((intLikeCount == null) || (intLikeCount == 0)) {
            imageViewLikes.setVisibility(View.GONE);
            textViewLikeCount.setVisibility(View.GONE);
            intLikeCount = 0;
        } else {
            imageViewLikes.setVisibility(View.GONE);
            textViewLikeCount.setVisibility(View.GONE);
        }

        if ((strGID == null) || (strGID.isEmpty()) ||
                (!Multicards.getPreferences().recentSets.containsKey(strGID)))
            textViewDate.setVisibility(View.GONE);
        else {
            textViewDate.setVisibility(View.GONE);
            strDate = Utils.getPrettyDate(Multicards.getPreferences().recentSets.get(strGID));
        }

        if ((strLangFrom == null) || (strLangFrom.isEmpty())
                || (strLangTo == null) || (strLangTo.isEmpty())) {
            imageViewLangFrom.setVisibility(View.GONE);
            imageViewLangTo.setVisibility(View.GONE);
        } else {
            imageViewLangFrom.setVisibility(View.VISIBLE);
            imageViewLangTo.setVisibility(View.VISIBLE);
        }

        textViewCardsetTitle.setText(strCardsetTitle);
        textViewLikeCount.setText(intLikeCount.toString());
        textViewAuthor.setText(strAuthor);
        textViewDate.setText(strDate);
        if (Utils.getCountryFlagByLang(strLangFrom) != null)
            imageViewLangFrom.setImageDrawable(Utils.getCountryFlagByLang(strLangFrom));

        if (Utils.getCountryFlagByLang(strLangTo) != null)
            imageViewLangTo.setImageDrawable(Utils.getCountryFlagByLang(strLangTo));

        return rowView;
    }
}
