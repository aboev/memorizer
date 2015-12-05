package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;

/**
 * Created by alex-mac on 22.11.15.
 */
public class MainMenuFragment extends Fragment {
    private Button buttonTrain;
    private Button buttonMultiplayer;
    private Button buttonSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_menu, container, false);

        buttonTrain = (Button) view.findViewById(R.id.buttonTrain);
        buttonMultiplayer = (Button) view.findViewById(R.id.buttonMultiplayer);
        buttonSettings = (Button) view.findViewById(R.id.buttonSettings);

        buttonTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemorizerApplication.getMainActivity().nextFlashCard();

                MemorizerApplication.getMainActivity().scoreView =
                        (TextView) MemorizerApplication.getMainActivity().
                                findViewById(R.id.scoreView);

                MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_TRAIN_MODE;

                MemorizerApplication.getMainActivity().showPlayersInfo();
            }
        });

        buttonMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiplayerInterface multiplayerInterface = new MultiplayerInterface();
                MemorizerApplication.setMultiPlayerInterface(multiplayerInterface);
                multiplayerInterface.requestNewGame();
                MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_MULTIPLAYER_MODE;
            }
        });

        MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;

        return view;
    }

    public void onButtonTrainClick (View v){

    }

}
