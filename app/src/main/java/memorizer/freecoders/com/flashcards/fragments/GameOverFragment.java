package memorizer.freecoders.com.flashcards.fragments;

/**
 * Created by alex-mac on 12.12.15.
 */

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.AnswerLogAdapter;
import memorizer.freecoders.com.flashcards.classes.AnswerLogView;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.GameOverMessage;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

public class GameOverFragment extends Fragment  {
    private static String LOG_TAG = "GameOverFragment";

    private View view;

    private LinearLayout buttonLikeCardset;
    private String strCardsetID;
    private GameOverMessage gameOverMessage;
    private TextView textViewWinnerName;
    private CircleImageView imageViewWinner;
    private TextView textViewWinner;
    private LinearLayout linearLayoutLog;
    private AnswerLogAdapter answerLogAdapter;
    RecyclerView recyclerViewAnswerLog;
    public static int INT_GAME_TYPE_SINGLEPLAYER = 0;
    public static int INT_GAME_TYPE_MULTIPLAYER = 1;
    public int INT_GAME_TYPE = INT_GAME_TYPE_MULTIPLAYER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_over, container, false);
        this.view = view;

        textViewWinnerName = (TextView) view.findViewById(R.id.textViewWinnerName);
        imageViewWinner = (CircleImageView) view.findViewById(R.id.imageViewWinner);
        textViewWinner = (TextView) view.findViewById(R.id.textViewWinner);
        linearLayoutLog = (LinearLayout) view.findViewById(R.id.linearLayoutLog);
        recyclerViewAnswerLog = (RecyclerView) view.findViewById(R.id.listViewLog);
        answerLogAdapter = new AnswerLogAdapter(Multicards.getMainActivity(),
                GameplayManager.currentGameplay);
        recyclerViewAnswerLog.setAdapter(answerLogAdapter);
        recyclerViewAnswerLog.setLayoutManager(
                new LinearLayoutManager(Multicards.getMainActivity()));
        recyclerViewAnswerLog.setItemAnimator(
                new FadeInLeftAnimator(new OvershootInterpolator(1f)));
        populateView();

        return view;
    }

    public void setCardsetID (String strCardsetID) {
        this.strCardsetID = strCardsetID;
    }

    public String getCardsetID () {return this.strCardsetID;}

    public void populateView() {
        buttonLikeCardset = (LinearLayout) view.findViewById(R.id.buttonLikeCardset);
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animations.alphaAnimation(buttonLikeCardset);
            }
        }, 2000);

        showGameOverMessage();

        populateGameLog();

    }

    public void setGameOverMessage (GameOverMessage msg) {
        gameOverMessage = msg;
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

    public GameOverMessage getGameOverMessage () {
        return this.gameOverMessage;
    }

    public void populateGameLog () {
        if (GameplayManager.currentGameplay != null) {
          for (int i = 0; i < GameplayManager.currentGameplay.questions.size(); i++) {
              final FlashCard question = GameplayManager.currentGameplay.questions.get(i);
              AnswerLogView answerItem = new AnswerLogView(Multicards.getMainActivity());
              answerItem.setText(question.question + " - " +
                      question.options.get(GameplayManager.currentGameplay.answers.get(i)));
              answerItem.setCorrect(GameplayManager.currentGameplay.checks.get(i));
              answerItem.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      String strText = question.question + " - " +
                              question.options.get(question.answer_id);
                      InputDialogInterface.showModalDialog(strText, null);
                  }
              });
              linearLayoutLog.addView(answerItem);
          }
        }
    }

    public static GameOverFragment cloneFragment (GameOverFragment fragment) {
        GameOverFragment newFragment = new GameOverFragment();
        newFragment.INT_GAME_TYPE = fragment.INT_GAME_TYPE;
        newFragment.setCardsetID(fragment.getCardsetID());
        newFragment.setGameOverMessage(fragment.getGameOverMessage());
        return newFragment;
    }

    public void animate () {
        Animations.alphaAnimation(buttonLikeCardset);
    }
}
