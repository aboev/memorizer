package memorizer.freecoders.com.flashcards.fragments;

/**
 * Created by alex-mac on 12.12.15.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

public class GameOverFragment extends Fragment {
    private static String LOG_TAG = "GameOverFragment";

    private View view;

    private Button buttonLikeCardset;
    private String strCardsetID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_over, container, false);
        this.view = view;

        populateView();

        return view;
    }

    public void setCardsetID (String strCardsetID) {
        this.strCardsetID = strCardsetID;
    }

    public void populateView() {
        buttonLikeCardset = (Button) view.findViewById(R.id.buttonLikeCardset);
        buttonLikeCardset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerInterface.likeCardsetRequest(strCardsetID, new Response.Listener<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        buttonLikeCardset.setEnabled(false);
                    }
                }, null);
            }
        });
    }
}
