package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.classes.StyleProgressBar;
import memorizer.freecoders.com.flashcards.common.Animations;
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

    StyleProgressBar styleProgressBar;

    TextView scoreView;

    View highlightUserCorrect, highlightUserWrong,
            highlightOpponentCorrect, highlightOpponentWrong;

    public ArrayList<Integer> scoreList;
    public ArrayList<String> playerNames;

    public int intTotalQuestions = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view;
        if (MemorizerApplication.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE)
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

        if ((MemorizerApplication.getPreferences().strUserName != null) &&
                (!MemorizerApplication.getPreferences().strUserName.isEmpty()))
            textViewPlayer1Name.setText(MemorizerApplication.getPreferences().strUserName);
        else
            textViewPlayer1Name.setText("Player1");

        if (MemorizerApplication.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE) {
            textViewPlayer2Name.setVisibility(View.GONE);
            imageViewPlayer2Avatar.setVisibility(View.GONE);
            styleProgressBar.setVisibility(View.GONE);
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
            styleProgressBar.setVisibility(View.VISIBLE);
            styleProgressBar.setProgress(0, false);
        }

        playerNames = new ArrayList<String>();
        playerNames.add(textViewPlayer1Name.getText().toString());
        playerNames.add(textViewPlayer2Name.getText().toString());

        scoreList = new ArrayList<Integer>();
        scoreList.add(0);
        scoreList.add(0);

        updateScore();

        intTotalQuestions = 0;

        return view;
    }

    public void updateScore()
    {
        if (MemorizerApplication.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE)
            scoreView.setText("Score: " + scoreList.get(0) + "/" + intTotalQuestions);
        else if (MemorizerApplication.getMainActivity().intUIState == Constants.UI_STATE_MULTIPLAYER_MODE) {
            scoreView.setText("Score: " + scoreList.get(0) + "/" + scoreList.get(1));
            int progress = 100 * scoreList.get(0) / Constants.GAMEPLAY_QUESTIONS_PER_GAME;
            styleProgressBar.setProgress(progress, true);
        }

    }

    public void increaseScore (int playerID) {
        scoreList.set(playerID, scoreList.get(playerID) + 1);
    }

    public void highlightAnswer (int playerID, Boolean boolCorrect) {
        if (MemorizerApplication.getMainActivity().intUIState ==
                Constants.UI_STATE_MULTIPLAYER_MODE) {

            if (playerID == 0)
                Animations.scaleAnimation(imageViewPlayer1Avatar);
            else
                Animations.scaleAnimation(imageViewPlayer2Avatar);
        }


        if (playerID == 0) {
            if (boolCorrect)
                Animations.alphaAnimation(highlightUserCorrect);
            else
                Animations.alphaAnimation(highlightUserWrong);
        } else {
            if (boolCorrect)
                Animations.alphaAnimation(highlightOpponentCorrect);
            else
                Animations.alphaAnimation(highlightOpponentWrong);
        }

    }
}
