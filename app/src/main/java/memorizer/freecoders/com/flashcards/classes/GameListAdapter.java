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
import memorizer.freecoders.com.flashcards.json.CardSet;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 22.01.16.
 */
public class GameListAdapter extends ArrayAdapter<String> {
    private Context context;
    public ArrayList<Game> values = new ArrayList<Game>();

    public GameListAdapter(Context context) {
        super(context, -1);
        this.context = context;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    public void setValues(ArrayList<Game> values) {
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_game, parent, false);

        CircleImageView imageView = (CircleImageView) rowView.findViewById(R.id.imageViewAvatar);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewCardsetName);
        TextView textViewPlayerName = (TextView) rowView.findViewById(R.id.textViewPlayerName);

        Game game = values.get(position);

        if ((game.cardset != null) && (game.cardset.title != null))
            textView.setText(game.cardset.title.toString());
        else
            textView.setText("");

        if (game.profiles != null) {
            UserDetails opponent = Utils.extractOpponentProfile(game.profiles);
            if (opponent != null) {
                if (opponent.avatar != null)
                    Multicards.getAvatarLoader().get(opponent.avatar,
                            new Utils.AvatarListener(imageView));
                if (opponent.name != null)
                    textViewPlayerName.setText(opponent.name.toString());
            }
        }

        return rowView;
    }
}
