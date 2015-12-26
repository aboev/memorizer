package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.content.Intent;
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
                Intent intent = new Intent(MemorizerApplication.getMainActivity(),
                        CardsetPickerActivity.class);
                intent.putExtra(Constants.INTENT_META_NEXT_FRAGMENT,
                        Constants.UI_STATE_TRAIN_MODE);
                startActivity(intent);
            }
        });

        textViewMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemorizerApplication.getMainActivity(),
                        CardsetPickerActivity.class);
                intent.putExtra(Constants.INTENT_META_NEXT_FRAGMENT,
                        Constants.UI_STATE_MULTIPLAYER_MODE);
                startActivity(intent);
            }
        });

        if (MemorizerApplication.getMainActivity() != null)
            MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;

        return view;
    }

}
