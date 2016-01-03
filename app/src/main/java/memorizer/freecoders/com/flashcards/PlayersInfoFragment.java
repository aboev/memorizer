package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.StyleProgressBar;
import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.Game;
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

    StyleProgressBar styleProgressBar;

    TextView scoreView;

    View highlightUserCorrect, highlightUserWrong,
            highlightOpponentCorrect, highlightOpponentWrong;

    public ArrayList<Integer> scoreList = new ArrayList<Integer>();
    public ArrayList<String> playerNames = new ArrayList<String>();

    public int intTotalQuestions = 0;

    public PlayersInfoFragment() {
        String strPlayer1Name = "Player1";
        String strPlayer2Name = "Player2";
        if ((Multicards.getPreferences().strUserName != null) &&
                (!Multicards.getPreferences().strUserName.isEmpty()))
            strPlayer1Name = Multicards.getPreferences().strUserName;

        if ((Multicards.getMultiplayerInterface() != null) &&
                (Multicards.getMultiplayerInterface().currentGame != null) &&
                (Multicards.getMultiplayerInterface().currentGame.game.profiles != null)) {
            HashMap<String,UserDetails> profiles = Multicards.
                    getMultiplayerInterface().currentGame.game.profiles;
            Iterator it = profiles.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String strSocketID = (String) pair.getKey();
                UserDetails userDetails = (UserDetails) pair.getValue();
                if ((!strSocketID.equals(Multicards.getPreferences().strSocketID)) &&
                        (userDetails.name != null) && (!userDetails.name.isEmpty()))
                    strPlayer2Name = userDetails.name;
                it.remove();
            }
        }

        playerNames.add(strPlayer1Name);
        playerNames.add(strPlayer2Name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view;
        if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE)
            view = inflater.inflate(R.layout.players_info_single, container, false);
        else
            view = inflater.inflate(R.layout.players_info_multi, container, false);

        textViewPlayer1Name = (TextView) view.findViewById(R.id.TextViewPlayer1Name);
        textViewPlayer2Name = (TextView) view.findViewById(R.id.TextViewPlayer2Name);

        imageViewPlayer1Avatar = (CircleImageView) view.findViewById(R.id.ImageViewPlayer1Avatar);
        imageViewPlayer2Avatar = (CircleImageView) view.findViewById(R.id.ImageViewPlayer2Avatar);

        highlightUserCorrect = view.findViewById(R.id.viewUserGreenHighlight);
        highlightUserWrong = view.findViewById(R.id.viewUserRedHighlight);

        highlightOpponentCorrect = view.findViewById(R.id.viewOpponentGreenHighlight);
        highlightOpponentWrong = view.findViewById(R.id.viewOpponentRedHighlight);

        scoreView = (TextView) view.findViewById(R.id.scoreView);

        styleProgressBar = (StyleProgressBar) view.findViewById(R.id.styleprogressbar);

        textViewPlayer1Name.setText(playerNames.get(0));

        if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE) {
            textViewPlayer2Name.setVisibility(View.GONE);
            imageViewPlayer2Avatar.setVisibility(View.GONE);
            styleProgressBar.setVisibility(View.GONE);
        } else if (Multicards.getMainActivity().intUIState ==
                Constants.UI_STATE_MULTIPLAYER_MODE) {
            textViewPlayer2Name.setText(playerNames.get(1));
            styleProgressBar.setVisibility(View.VISIBLE);
            styleProgressBar.setProgress(0, false);
        }

        scoreList.add(0);
        scoreList.add(0);

        updateScore();

        intTotalQuestions = 0;

        return view;
    }

    public void updateScore()
    {
        if ( (scoreList != null) && ( scoreList.size() > 0) ) {
            if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE)
                scoreView.setText("Score: " + scoreList.get(0) + "/" + intTotalQuestions);
            else if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_MULTIPLAYER_MODE) {
                scoreView.setText("Score: " + scoreList.get(0) + "/" + scoreList.get(1));
                int progress = 100 * scoreList.get(0) / Constants.GAMEPLAY_QUESTIONS_PER_GAME;
                styleProgressBar.setProgress(progress, true);
            }
        }

        if (playerNames.size() > 1) {
            if (textViewPlayer1Name != null)
                textViewPlayer1Name.setText(playerNames.get(0));
            if (textViewPlayer2Name != null)
                textViewPlayer2Name.setText(playerNames.get(1));
        }

    }

    public void increaseScore (int playerID) {
        scoreList.set(playerID, scoreList.get(playerID) + 1);
    }

    public void highlightAnswer (int playerID, Boolean boolCorrect, CallbackInterface onAnimationEnd) {
        if (Multicards.getMainActivity().intUIState ==
                Constants.UI_STATE_MULTIPLAYER_MODE) {

            if (playerID == 0)
                Animations.scaleAnimation(imageViewPlayer1Avatar, onAnimationEnd);
            else
                Animations.scaleAnimation(imageViewPlayer2Avatar, onAnimationEnd);
        }
    }

    public void updateGameInfo (Game game) {
        HashMap<String,UserDetails> profiles = game.profiles;
        Iterator it = profiles.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String strSocketID = (String) pair.getKey();
            UserDetails userDetails = (UserDetails) pair.getValue();
            if ((!strSocketID.equals(Multicards.getPreferences().strSocketID)) &&
                    (userDetails.name != null) && (!userDetails.name.isEmpty()))
                playerNames.set(1, userDetails.name);
            it.remove();
        }
        if (textViewPlayer1Name != null)
            textViewPlayer1Name.setText(playerNames.get(0));
        if (textViewPlayer2Name != null)
            textViewPlayer2Name.setText(playerNames.get(1));
    }
}
