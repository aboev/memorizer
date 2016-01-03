package memorizer.freecoders.com.flashcards.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import memorizer.freecoders.com.flashcards.CardsetPickerActivity;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;

/**
 * Created by alex-mac on 22.11.15.
 */
public class MainMenuFragment extends Fragment {
    private TextView textViewTrain;
    private TextView textViewMultiplayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.main_menu, container, false);

        textViewTrain = (TextView) view.findViewById(R.id.TextView_StyleButtonTrain);
        textViewMultiplayer = (TextView) view.findViewById(R.id.TextView_StyleButtonMultiplayer);

        textViewTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Multicards.getMainActivity(),
                        CardsetPickerActivity.class);
                intent.putExtra(Constants.INTENT_META_NEXT_FRAGMENT,
                        Constants.UI_STATE_TRAIN_MODE);
                startActivity(intent);
            }
        });

        textViewMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Multicards.getMainActivity(),
                        CardsetPickerActivity.class);
                intent.putExtra(Constants.INTENT_META_NEXT_FRAGMENT,
                        Constants.UI_STATE_MULTIPLAYER_MODE);
                startActivity(intent);
            }
        });

        if (Multicards.getMainActivity() != null)
            Multicards.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;

        return view;
    }

}
