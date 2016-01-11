package memorizer.freecoders.com.flashcards.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.internal.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.StyleProgressBar;
import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.StringRequest;
import memorizer.freecoders.com.flashcards.utils.Utils;

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

    public String player1Name = "";
    public String player2Name = "";
    public Integer player1Score = 0;
    public Integer player2Score = 0;

    public int intTotalQuestions = 0;

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

        if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE) {
            textViewPlayer2Name.setVisibility(View.GONE);
            imageViewPlayer2Avatar.setVisibility(View.GONE);
            styleProgressBar.setVisibility(View.GONE);
        } else if (Multicards.getMainActivity().intUIState ==
                Constants.UI_STATE_MULTIPLAYER_MODE) {
            textViewPlayer2Name.setText(player2Name);
            styleProgressBar.setVisibility(View.VISIBLE);
            styleProgressBar.setProgress(0, false);
        }

        initInfo();
        updateInfo();

        textViewPlayer1Name.setText(player1Name);
        textViewPlayer2Name.setText(player2Name);

        return view;
    }

    public void initInfo() {
        player1Name = "Player 1";
        player1Name = "Player 2";

        if ((Multicards.getPreferences().strUserName != null) &&
                (!Multicards.getPreferences().strUserName.isEmpty()))
            player1Name = Multicards.getPreferences().strUserName;

        if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_MULTIPLAYER_MODE) {
            Game currentGame = Multicards.getMultiplayerInterface().currentGame.game;
            if (Utils.extractOpponentProfile(currentGame.profiles) != null)
                player2Name = Utils.extractOpponentProfile(currentGame.profiles).name;
        }

        player1Score = 0;
        player2Score = 0;

        intTotalQuestions = 0;
    }

    public void updateInfo()
    {
        if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE)
            scoreView.setText("Score: " + player1Score + "/" + intTotalQuestions);
        else if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_MULTIPLAYER_MODE) {
            HashMap<String, Integer> scores = Multicards.getMultiplayerInterface().currentScores;
            if (scores != null) {
                player1Score = scores.get(Multicards.getPreferences().strSocketID);
                player2Score = Utils.extractOpponentScore(scores);
            }

            Log.d(LOG_TAG, "Socket id "+Multicards.getPreferences().strSocketID);
            Log.d(LOG_TAG, "Setting scores "+player1Score+"-"+player2Score);
            if ((scoreView != null) && (player1Score != null)) {
                Log.d(LOG_TAG, "Setting scores "+player1Score+"-"+player2Score);
                scoreView.setText("Score: " + player1Score + "/" + player2Score);
                int progress = 100 * player1Score / Constants.GAMEPLAY_QUESTIONS_PER_GAME;
                styleProgressBar.setProgress(progress, true);
            }
        }
    }

    public void eventPlayer1Answer (Boolean boolAnswerCorrect) {
        if (boolAnswerCorrect) {
            intTotalQuestions++;
            player1Score++;
        } else
            intTotalQuestions++;
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
}
