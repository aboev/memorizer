package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 22.01.16.
 */
public class OpponentListAdapter extends ArrayAdapter<String> {
    private Context context;
    public ArrayList<UserDetails> values = new ArrayList<UserDetails>();

    public OpponentListAdapter(Context context) {
        super(context, -1);
        this.context = context;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    public void setValues(ArrayList<UserDetails> values) {
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_user, parent, false);

        CircleImageView imageView = (CircleImageView) rowView.findViewById(R.id.imageViewAvatar);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewName);

        UserDetails user = values.get(position);

        textView.setText(user.name);
        if (user.avatar != null)
            Multicards.getAvatarLoader().get(user.avatar, new Utils.AvatarListener(imageView));

        return rowView;
    }
}
