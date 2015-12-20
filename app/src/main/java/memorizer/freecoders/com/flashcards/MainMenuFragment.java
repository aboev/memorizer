package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;

import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;

/**
 * Created by alex-mac on 22.11.15.
 */
public class MainMenuFragment extends Fragment {
    private Button buttonTrain;
    private Button buttonMultiplayer;
    private Button buttonSettings;

    private TextView textViewTrain;
    private TextView textViewMultiplayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.main_menu, container, false);

        buttonTrain = (Button) view.findViewById(R.id.buttonTrain);
        buttonMultiplayer = (Button) view.findViewById(R.id.buttonMultiplayer);
        buttonSettings = (Button) view.findViewById(R.id.buttonSettings);

        textViewTrain = (TextView) view.findViewById(R.id.TextView_StyleButtonTrain);
        textViewMultiplayer = (TextView) view.findViewById(R.id.TextView_StyleButtonMultiplayer);

        textViewTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemorizerApplication.getMainActivity().cardsetPickerFragment = new CardsetPickerFragment();
                MemorizerApplication.getMainActivity().showNextFragment(
                        MemorizerApplication.getMainActivity().cardsetPickerFragment,
                        FragmentTransactionExtended.SLIDE_HORIZONTAL);
                MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_CARD_PICK;
                MemorizerApplication.getMainActivity().cardsetPickerFragment.intNextFragment =
                        Constants.UI_STATE_TRAIN_MODE;

                /*
                MemorizerApplication.getMainActivity().nextFlashCard();

                MemorizerApplication.getMainActivity().scoreView =
                        (TextView) MemorizerApplication.getMainActivity().
                                findViewById(R.id.scoreView);

                MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_TRAIN_MODE;

                MemorizerApplication.getMainActivity().showPlayersInfo();
                */
            }
        });

        textViewMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemorizerApplication.getMainActivity().cardsetPickerFragment = new CardsetPickerFragment();
                MemorizerApplication.getMainActivity().showNextFragment(
                        MemorizerApplication.getMainActivity().cardsetPickerFragment,
                        FragmentTransactionExtended.SLIDE_HORIZONTAL);
                MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_CARD_PICK;
                MemorizerApplication.getMainActivity().cardsetPickerFragment.intNextFragment =
                        Constants.UI_STATE_MULTIPLAYER_MODE;
                /*

                MultiplayerInterface multiplayerInterface = new MultiplayerInterface();
                MemorizerApplication.setMultiPlayerInterface(multiplayerInterface);
                multiplayerInterface.requestNewGame(null);
                */
            }
        });


        MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;

        return view;
    }

    public void onButtonTrainClick (View v){

    }

}
