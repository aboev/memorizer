package memorizer.freecoders.com.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import memorizer.freecoders.com.flashcards.classes.AnswerLogAdapter;
import memorizer.freecoders.com.flashcards.classes.AnswerLogView;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.classes.GameplayData;
import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.fragments.RecentCardsetsFragment;
import memorizer.freecoders.com.flashcards.fragments.SearchCardsetFragment;
import memorizer.freecoders.com.flashcards.json.GameOverMessage;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 31.01.16.
 */
public class GameOverActivity extends AppCompatActivity {

    private static String LOG_TAG = "GameOverActivity";

    private LinearLayout buttonLikeCardset;
    private String strCardsetID;
    private GameOverMessage gameOverMessage;
    private TextView textViewWinnerName;
    private CircleImageView imageViewWinner;
    private TextView textViewWinner;
    private TextView textViewScore;
    private LinearLayout linearLayoutLog;
    private AnswerLogAdapter answerLogAdapter;
    RecyclerView recyclerViewAnswerLog;
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
        imageViewWinner = (CircleImageView) findViewById(R.id.imageViewWinner);
        textViewWinner = (TextView) findViewById(R.id.textViewWinner);
        textViewScore = (TextView) findViewById(R.id.textViewScore);
        linearLayoutLog = (LinearLayout) findViewById(R.id.linearLayoutLog);
        recyclerViewAnswerLog = (RecyclerView) findViewById(R.id.listViewLog);
        answerLogAdapter = new AnswerLogAdapter(Multicards.getMainActivity(),
                GameplayManager.currentGameplay);
        answerLogAdapter.initStartDelay();
        recyclerViewAnswerLog.setAdapter(answerLogAdapter);
        recyclerViewAnswerLog.setLayoutManager(
                new LinearLayoutManager(Multicards.getMainActivity()));
        recyclerViewAnswerLog.setItemAnimator(
                new FadeInLeftAnimator(new OvershootInterpolator(1f)));

        Intent intent = getIntent();
        strCardsetID = intent.getStringExtra(Constants.INTENT_META_SET_ID);
        INT_GAME_TYPE = intent.getIntExtra(Constants.INTENT_META_GAME_TYPE,
                INT_GAME_TYPE_SINGLEPLAYER);
        if (intent.hasExtra(Constants.INTENT_META_GAMEOVER_MESSAGE))
            gameOverMessage = gson.fromJson(intent.getStringExtra(
                    Constants.INTENT_META_GAMEOVER_MESSAGE), GameOverMessage.class);

        populateView();

        Multicards.setGameOverActivity(this);
    }

    public void populateView() {
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

        if (INT_GAME_TYPE == INT_GAME_TYPE_MULTIPLAYER) {
            textViewWinnerName.setVisibility(View.VISIBLE);
            textViewWinner.setVisibility(View.VISIBLE);
            imageViewWinner.setVisibility(View.VISIBLE);
        } else if (INT_GAME_TYPE == INT_GAME_TYPE_SINGLEPLAYER) {
            textViewWinnerName.setVisibility(View.GONE);
            textViewWinner.setVisibility(View.GONE);
            imageViewWinner.setVisibility(View.GONE);
        }

        intUserScore = Multicards.getPreferences().intUserScore;
        textViewScore.setText(String.valueOf(intUserScore));

        showGameOverMessage();

        populateGameLog();

    }

    public void showGameOverMessage () {
        if (gameOverMessage != null) {
            if (gameOverMessage.winner.name != null) {
                textViewWinnerName.setText(gameOverMessage.winner.name);
                if (gameOverMessage.winner.name.equals(Multicards.getPreferences().strUserName))
                    textViewWinner.setText(getResources().getString(R.string.string_winner_you));
                else
                    textViewWinner.setText(getResources().getString(R.string.string_winner));
            }
            if (gameOverMessage.winner.avatar != null)
                Multicards.getAvatarLoader().get(gameOverMessage.winner.avatar,
                        new Utils.AvatarListener(imageViewWinner));
        }
    }

    public void populateGameLog () {
        if (GameplayManager.currentGameplay != null) {

            if (GameplayManager.currentGameplay.intGameType == GameplayData.INT_SINGLEPLAYER) {
                answerLogAdapter.setAddItemCallback(new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        int pos = (int) obj;
                        if (GameplayManager.currentGameplay.checks.get(pos)) {
                            intUserScore++;
                            textViewScore.setText(String.valueOf(intUserScore));
                            Animations.scaleAnimation(textViewScore, null);

                            Multicards.getPreferences().intUserScore = intUserScore;
                            Multicards.getPreferences().savePreferences();
                        }
                    }
                });
            }

            for (int i = 0; i < GameplayManager.currentGameplay.questions.size(); i++ ) {
                delayedHandler(new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        int pos = (int) obj;
                        answerLogAdapter.addItem();
                    }
                }, i);
            }

            recyclerViewAnswerLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

    private void delayedHandler (final CallbackInterface onCallback, final int position) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onCallback.onResponse(position);
            }
        }, 500);
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

}
