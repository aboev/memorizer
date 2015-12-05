package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.json.UserDetails;

/**
 * Created by alex-mac on 05.12.15.
 */
public class PlayersInfoFragment extends Fragment{
    private static String LOG_TAG = "FlashCardFragment";

    TextView textViewPlayer1Name;
    TextView textViewPlayer2Name;

    CircleImageView imageViewPlayer1Avatar;
    CircleImageView imageViewPlayer2Avatar;

    TextView scoreView;

    ArrayList<Integer> scoreList;

    public int intTotalQuestions = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.players_info, container, false);

        textViewPlayer1Name = (TextView) view.findViewById(R.id.TextViewPlayer1Name);
        textViewPlayer2Name = (TextView) view.findViewById(R.id.TextViewPlayer2Name);

        imageViewPlayer1Avatar = (CircleImageView) view.findViewById(R.id.ImageViewPlayer1Avatar);
        imageViewPlayer2Avatar = (CircleImageView) view.findViewById(R.id.ImageViewPlayer2Avatar);

        scoreView = (TextView) view.findViewById(R.id.scoreView);

        if ((MemorizerApplication.getPreferences().strUserName != null) &&
                (!MemorizerApplication.getPreferences().strUserName.isEmpty()))
            textViewPlayer1Name.setText(MemorizerApplication.getPreferences().strUserName);
        else
            textViewPlayer1Name.setText("Player1");

        if (MemorizerApplication.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE) {
            textViewPlayer2Name.setVisibility(View.GONE);
            imageViewPlayer2Avatar.setVisibility(View.GONE);
        } else if (MemorizerApplication.getMainActivity().intUIState ==
                Constants.UI_STATE_MULTIPLAYER_MODE) {
            if ((MemorizerApplication.getMultiplayerInterface() != null) &&
                    (MemorizerApplication.getMultiplayerInterface().currentGame != null) &&
                    (MemorizerApplication.getMultiplayerInterface().currentGame.profiles != null)) {
                HashMap<String,UserDetails> profiles = MemorizerApplication.
                        getMultiplayerInterface().currentGame.profiles;
                Iterator it = profiles.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    String strSocketID = (String) pair.getKey();
                    UserDetails userDetails = (UserDetails) pair.getValue();
                    if ((!strSocketID.equals(MemorizerApplication.getPreferences().strSocketID)) &&
                            (userDetails.name != null) && (!userDetails.name.isEmpty()))
                        textViewPlayer2Name.setText(userDetails.name);
                    it.remove();
                }
            }
        }

        scoreList = new ArrayList<Integer>();
        scoreList.add(0);
        scoreList.add(0);

        updateScore();

        intTotalQuestions = 0;

        return view;
    }

    public void updateScore()
    {
        if (MemorizerApplication.getMainActivity().intUIState == Constants.UI_STATE_MAIN_MENU)
            scoreView.setText("Score: " + scoreList.get(0) + "/" + intTotalQuestions);
        else if (MemorizerApplication.getMainActivity().intUIState == Constants.UI_STATE_MULTIPLAYER_MODE)
            scoreView.setText("Score: " + scoreList.get(0) + "/" + scoreList.get(1));
    }

    public void increaseScore (int playerID) {
        scoreList.set(playerID, scoreList.get(playerID) + 1);
    }
}
