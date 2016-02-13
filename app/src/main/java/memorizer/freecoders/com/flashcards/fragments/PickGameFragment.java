package memorizer.freecoders.com.flashcards.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.GameListAdapter;
import memorizer.freecoders.com.flashcards.classes.OpponentListAdapter;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 10.02.16.
 */
public class PickGameFragment extends DialogFragment {

    private static String LOG_TAG = "PickGameFragment";

    ListView gamesList;
    GameListAdapter gamesListAdapter;
    Button buttonCancel;
    CallbackInterface onClickOK;

    public PickGameFragment () {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_game, container);
        gamesList = (ListView) view.findViewById(R.id.listViewPickGame);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

        gamesListAdapter = new GameListAdapter(Multicards.getMainActivity());

        ServerInterface.getPendingGamesRequest(new Response.Listener<ArrayList<Game>>() {
            @Override
            public void onResponse(ArrayList<Game> response) {
                gamesListAdapter.setValues(response);
                gamesList.setAdapter(gamesListAdapter);
            }
        }, null);

        gamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (gamesListAdapter.values.get(position).profiles != null) {
                    UserDetails opponent = Utils.extractOpponentProfile
                            (gamesListAdapter.values.get(position).profiles);
                    if ((opponent != null) && (opponent.name != null)) {
                        onClickOK.onResponse(opponent.name);
                        updateUserDetailsCache(opponent.name);
                    }
                }
                dismissFragment();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissFragment();
            }
        });

        return view;
    }

    public void setOnClickOKListener (CallbackInterface callback) {
        this.onClickOK = callback;
    }

    public void dismissFragment () {
        Fragment prev = Multicards.getMainActivity().
                getFragmentManager().findFragmentByTag(Constants.TAG_PICK_GAME_FRAGMENT);
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }

    public void updateUserDetailsCache (String strUsername) {
        ServerInterface.getUserProfileByName(strUsername, new Response.Listener<UserDetails>() {
            @Override
            public void onResponse(UserDetails response) {
                Multicards.getPreferences().saveRecentOpponent(response);
            }
        }, null);
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

}
