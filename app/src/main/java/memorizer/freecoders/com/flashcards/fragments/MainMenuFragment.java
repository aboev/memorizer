package memorizer.freecoders.com.flashcards.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import memorizer.freecoders.com.flashcards.CardsetPickerActivity;
import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 22.11.15.
 */
public class MainMenuFragment extends Fragment {
    private TextView textViewTrain;
    private TextView textViewMultiplayer;
    private TextView textViewSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        textViewTrain = (TextView) view.findViewById(R.id.TextView_StyleButtonTrain);
        textViewMultiplayer = (TextView) view.findViewById(R.id.TextView_StyleButtonMultiplayer);
        textViewSettings = (TextView) view.findViewById(R.id.TextView_StyleButtonSettings);

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
                InputDialogInterface.showPickOpponentDialog(new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        Integer index = (Integer) obj;
                        if (index == 0) {
                            Intent intent = new Intent(Multicards.getMainActivity(),
                                    CardsetPickerActivity.class);
                            intent.putExtra(Constants.INTENT_META_NEXT_FRAGMENT,
                                    Constants.UI_STATE_MULTIPLAYER_MODE);
                            startActivity(intent);
                        } else if (index == 1) {
                            GameplayManager.strOpponentName = "-1";
                            Intent intent = new Intent(Multicards.getMainActivity(),
                                    CardsetPickerActivity.class);
                            intent.putExtra(Constants.INTENT_META_NEXT_FRAGMENT,
                                    Constants.UI_STATE_MULTIPLAYER_MODE);
                            startActivity(intent);
                        } else if (index == 2) {
                            InputDialogInterface.showEnterOpponentNameDialog(new CallbackInterface() {
                                @Override
                                public void onResponse(Object obj) {
                                    if (obj != null) {
                                        String strOpponentName = (String) obj;
                                        GameplayManager.strOpponentName = strOpponentName;
                                        ServerInterface.newGameRequest("", strOpponentName,
                                                null, null);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        textViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager.showUserProfileFragment();
            }
        });

        if (Multicards.getMainActivity() != null)
            Multicards.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;

        return view;
    }

}
