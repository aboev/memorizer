package memorizer.freecoders.com.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.classes.AchievementLogManager;
import memorizer.freecoders.com.flashcards.classes.AnswerLogManager;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.BonusDescriptor;
import memorizer.freecoders.com.flashcards.json.GameOverMessage;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 31.01.16.
 */
public class GameOverActivity extends AppCompatActivity {

    private static String LOG_TAG = "GameOverActivity";

    private String strCardsetID;
    private GameOverMessage gameOverMessage;

    private LinearLayout buttonLikeCardset;
    private LinearLayout linearLayoutAnswers;
    private LinearLayout linearLayoutAchievements;

    private TextView textViewWinner;
    private TextView textViewWinnerName;
    private TextView textViewAchievements;
    private TextView textViewScore;
    private TextView textViewScoreValue;

    private CircleImageView imageViewWinner;

    private AchievementLogManager achievementLogManager;
    private AnswerLogManager answerLogManager;

    public static int INT_GAME_TYPE_SINGLEPLAYER = 0;
    public static int INT_GAME_TYPE_MULTIPLAYER = 1;
    public int INT_GAME_TYPE = INT_GAME_TYPE_MULTIPLAYER;
    private int intUserScore = 0;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        textViewWinnerName = (TextView) findViewById(R.id.textViewWinnerName);
        textViewWinner = (TextView) findViewById(R.id.textViewWinner);
        textViewScore = (TextView) findViewById(R.id.textViewScore);
        textViewScoreValue = (TextView) findViewById(R.id.textViewScoreValue);
        textViewAchievements = (TextView) findViewById(R.id.textViewAchievements);

        imageViewWinner = (CircleImageView) findViewById(R.id.imageViewWinner);

        linearLayoutAnswers = (LinearLayout) findViewById(R.id.linearLayoutAnswers);
        linearLayoutAchievements = (LinearLayout) findViewById(R.id.linearLayoutAchievements);

        answerLogManager = new AnswerLogManager(this, GameplayManager.currentGameplay,
                linearLayoutAnswers);
        achievementLogManager = new AchievementLogManager(this, linearLayoutAchievements);

        Intent intent = getIntent();
        strCardsetID = intent.getStringExtra(Constants.INTENT_META_SET_ID);
        INT_GAME_TYPE = intent.getIntExtra(Constants.INTENT_META_GAME_TYPE,
                INT_GAME_TYPE_SINGLEPLAYER);
        Log.d(LOG_TAG, "GameOverActivity type " + INT_GAME_TYPE);
        if (intent.hasExtra(Constants.INTENT_META_GAMEOVER_MESSAGE))
            gameOverMessage = gson.fromJson(intent.getStringExtra(
                    Constants.INTENT_META_GAMEOVER_MESSAGE), GameOverMessage.class);

        if ((INT_GAME_TYPE == INT_GAME_TYPE_MULTIPLAYER) ||
                (FragmentManager.setUIStates.contains(Constants.UI_STATE_MULTIPLAYER_MODE))) {
            textViewWinnerName.setVisibility(View.VISIBLE);
            textViewWinner.setVisibility(View.VISIBLE);
            imageViewWinner.setVisibility(View.VISIBLE);
            textViewScore.setText(getResources().getString(R.string.string_score));

            if ((gameOverMessage != null) && (gameOverMessage.scores_before != null) &&
                    (gameOverMessage.scores_before.containsKey
                            (Multicards.getPreferences().strSocketID))) {
                intUserScore = gameOverMessage.scores_before.
                        get(Multicards.getPreferences().strSocketID);
                textViewScoreValue.setText(String.valueOf(intUserScore));
            }
        } else if ((INT_GAME_TYPE == INT_GAME_TYPE_SINGLEPLAYER)||
                (FragmentManager.setUIStates.contains(Constants.UI_STATE_TRAIN_MODE))) {
            textViewWinnerName.setVisibility(View.GONE);
            textViewWinner.setVisibility(View.GONE);
            imageViewWinner.setVisibility(View.GONE);
            intUserScore = Multicards.getPreferences().intUserScore;
            textViewScoreValue.setText("0");
            textViewScore.setText(getResources().getString(R.string.string_new_words));
        }

        buttonLikeCardset = (LinearLayout) findViewById(R.id.buttonLikeCardset);
        buttonLikeCardset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerInterface.likeCardsetRequest(strCardsetID, new Response.Listener<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        buttonLikeCardset.setClickable(false);
                        buttonLikeCardset.setEnabled(false);
                        buttonLikeCardset.setPressed(true);
                    }
                }, null);
            }
        });

        populateView();

        Multicards.setGameOverActivity(this);

    }

    public void populateView() {

        answerLogManager.setAnimationCallback(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                Integer intScore = Integer.valueOf(textViewScoreValue.getText().toString());
                intScore++;
                textViewScoreValue.setText(intScore.toString());
                Animations.scaleAnimation(textViewScoreValue, null);
            }
        });
        answerLogManager.setFinalAnimationCallback(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                populateAchievements();
            }
        });

        answerLogManager.populateView(true);

        if (gameOverMessage != null) {
            if ((gameOverMessage.winner != null) && (gameOverMessage.winner.name != null)) {
                textViewWinnerName.setText(gameOverMessage.winner.name);
                if (gameOverMessage.winner.name.equals(Multicards.getPreferences().strUserName)) {
                    textViewWinner.setText(getResources().getString(R.string.string_winner_you));
                    textViewWinnerName.setVisibility(View.GONE);
                } else
                    textViewWinner.setText(getResources().getString(R.string.string_winner));
            }
            if (gameOverMessage.winner.avatar != null)
                Multicards.getAvatarLoader().get(gameOverMessage.winner.avatar,
                        new Utils.AvatarListener(imageViewWinner));
        }
    }

    public void populateAchievements () {
        if ((gameOverMessage != null) && (gameOverMessage.bonuses != null) &&
                (gameOverMessage.bonuses.containsKey(Multicards.getPreferences().strSocketID))) {
            linearLayoutAchievements.setVisibility(View.VISIBLE);
            textViewAchievements.setVisibility(View.VISIBLE);

            ArrayList<BonusDescriptor> bonuses =
                    gameOverMessage.bonuses.get(Multicards.getPreferences().strSocketID);

            achievementLogManager.setValues(bonuses);

            achievementLogManager.populateView(new CallbackInterface() {
                @Override
                public void onResponse(Object obj) {
                    Integer bonus = (Integer) obj;
                    Integer intScore = Integer.valueOf(textViewScoreValue.getText().toString());
                    intScore = intScore + bonus;
                    textViewScoreValue.setText(intScore.toString());
                    Animations.scaleAnimation(textViewScoreValue, null);
                }
            }, new CallbackInterface() {
                @Override
                public void onResponse(Object obj) {
                    setFinalScore();
                }
            });
        } else {
            linearLayoutAchievements.setVisibility(View.GONE);
            textViewAchievements.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            FragmentManager.returnToMainMenu(false);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setFinalScore () {
        if ((gameOverMessage != null) && (gameOverMessage.scores != null) &&
                (gameOverMessage.scores.containsKey(Multicards.getPreferences().strSocketID))) {
            intUserScore = gameOverMessage.scores.get(Multicards.getPreferences().strSocketID);
            textViewScoreValue.setText(String.valueOf(intUserScore));
            Animations.scaleAnimation(textViewScoreValue, null);
        }
    }

}
