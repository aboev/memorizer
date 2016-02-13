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
import com.google.gson.Gson;
import com.jakewharton.disklrucache.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.OpponentListAdapter;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 23.01.16.
 */
public class PickOpponentFragment extends DialogFragment {

    private static String LOG_TAG = "PickOpponentFragment";

    EditText opponentNameText;
    ListView recentOpponentsList;
    OpponentListAdapter opponentListAdapter;
    Button buttonOK;
    Button buttonCancel;
    CallbackInterface onClickOK;

    public PickOpponentFragment () {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_opponent, container);
        opponentNameText = (EditText) view.findViewById(R.id.editTextOpponentName);
        recentOpponentsList = (ListView) view.findViewById(R.id.listViewPickOpponent);
        buttonOK = (Button) view.findViewById(R.id.buttonOK);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

        opponentListAdapter = new OpponentListAdapter(Multicards.getMainActivity());
        ArrayList<UserDetails> values = getRecentOpponents();
        UserDetails randomOpponent = new UserDetails();
        randomOpponent.name = Multicards.getMainActivity().getResources().getString(
                R.string.string_random_opponent);
        values.add(randomOpponent);
        opponentListAdapter.setValues(values);

        Log.d(LOG_TAG, "Set recent opponents count " + getRecentOpponents().size());

        recentOpponentsList.setAdapter(opponentListAdapter);

        recentOpponentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strOpponentName = null;
                if (position < (opponentListAdapter.getCount() - 1)) {
                    strOpponentName = opponentListAdapter.values.get(position).name;
                    updateUserDetailsCache(strOpponentName);
                }
                onClickOK.onResponse(strOpponentName);
                dismissFragment();
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strOpponentName = opponentNameText.getText().toString();
                if (!strOpponentName.isEmpty()) {
                    onClickOK.onResponse(strOpponentName);
                    updateUserDetailsCache(strOpponentName);
                    dismissFragment();
                } else
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

    private ArrayList<UserDetails> getRecentOpponents () {
        ArrayList<UserDetails> res = new ArrayList<UserDetails>();
        Map<Integer, Long> map = Utils.sortHashByValues(Multicards.getPreferences().recentOpponents);
        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String strUserID = (String) entry.getKey();
            if (Multicards.getPreferences().userDetailsCache.containsKey(strUserID))
                res.add(Multicards.getPreferences().userDetailsCache.get(strUserID));
        }
        return res;
    }

    public void setOnClickOKListener (CallbackInterface callback) {
        this.onClickOK = callback;
    }

    public void dismissFragment () {
        Fragment prev = Multicards.getMainActivity().
                getFragmentManager().findFragmentByTag(Constants.TAG_PICK_OPPONENT_FRAGMENT);
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
