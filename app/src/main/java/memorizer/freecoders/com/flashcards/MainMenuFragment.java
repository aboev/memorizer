package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;

import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

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
                // Create a new Fragment to be placed in the activity layout
                MemorizerApplication.getFlashCardActivity().currentFlashCardFragment =
                        new FlashCardFragment();

                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, MemorizerApplication.getFlashCardActivity().
                                currentFlashCardFragment).commit();

                MemorizerApplication.getFlashCardActivity().scoreView =
                        (TextView) MemorizerApplication.getFlashCardActivity().
                                findViewById(R.id.scoreView);
                MemorizerApplication.getFlashCardActivity().updateScore(0, 0);
            }
        });

        buttonMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemorizerApplication.getFlashCardActivity().multiplayerInterface.startGame();
            }
        });

        return view;
    }

    public void onButtonTrainClick (View v){

    }

}
