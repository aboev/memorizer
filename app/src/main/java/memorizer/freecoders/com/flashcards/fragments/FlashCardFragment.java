package memorizer.freecoders.com.flashcards.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.CardsetPickerActivity;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.AutoResizeTextView;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.classes.ListViewAdapter;
import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 07.11.15.
 */
public class FlashCardFragment extends Fragment {

    private static String LOG_TAG = "FlashCardFragment";

    public final static int INT_SHOW_ANSWER = 0;   // Show user mistake and correct answer
    public final static int INT_LOCAL_FLASHCARD = 1; // Show new flashcard (default)
    public final static int INT_SERVER_FLASHCARD = 2; // Show server flashcard

    private View view;

    private AdapterView.OnItemClickListener onFlashCardItemClickListener;

    public FlashCard mFlashCard;
    public CallbackInterface onAnswerPick;
    ListView flashCardsListView;
    public ListViewAdapter listViewAdapter;
    TextView questionTextView;
    ImageView questionImageView;
    private int intActionType = INT_LOCAL_FLASHCARD;


    public void setActionType(int intActionType) {
        this.intActionType = intActionType;
    }

    public void setFlashCard(FlashCard flashCard) {
        this.mFlashCard = flashCard;
    }

    public void setAnswerCallback(CallbackInterface callback) {
        this.onAnswerPick = callback;
    }

    public FlashCard getFlashCard (){
        return this.mFlashCard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.flashcard, container, false);
        this.view = view;

        populateView();

        return view;
    }

    public void wrongAnswerNotify()
    {
        Snackbar snackbar = Snackbar
                .make(view, "Wrong! Try again", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#f7df65"));
        TextView textSnackView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textSnackView.setTextColor(Color.parseColor("#e46d6f"));
        snackbar.show();
    }

    public void answerHighlight(final int intAnswerID, final Boolean boolOpponentAnswer,
                                CallbackInterface onComplete) {
        /*
            Visual animation of opponent answer
         */
        Log.d(LOG_TAG, "Highlighting answer " + intAnswerID);

        if (mFlashCard.answer_id == intAnswerID)
            listViewAdapter.setCorrectAnswer(intAnswerID);
        else
            listViewAdapter.setWrongAnswer(intAnswerID);

        int firstListItemPosition = flashCardsListView.getFirstVisiblePosition();
        int lastListItemPosition = firstListItemPosition + flashCardsListView.getChildCount();
        int childIndex = intAnswerID - firstListItemPosition
                + flashCardsListView.getHeaderViewsCount();
        if ((childIndex >= firstListItemPosition) && (childIndex <= lastListItemPosition)) {

            View option = flashCardsListView.getChildAt(childIndex);

            if (option == null) return;

            TextView textView = (TextView) option.findViewById(R.id.TextView_ButtonName);
            int colorFrom = Color.argb(0, 0, 255, 0);
            int colorTo = Color.argb(255, 0, 255, 0); // Green (correct answer)
            if (mFlashCard.answer_id != intAnswerID)
                colorTo = Color.argb(255, 255, 0, 0); // Red (wrong answer)

            Animations.highlightColor(textView, colorFrom, colorTo, onComplete);
        }
    }


    public boolean populateView() {
        flashCardsListView = (ListView) view.findViewById(R.id.ListView_FlashCard);
        listViewAdapter = new ListViewAdapter(view.getContext());

        LayoutInflater inflater = Multicards.getMainActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.flashcard_header,
                flashCardsListView, false);
        questionTextView = (TextView) header.findViewById(R.id.textViewQuestion);
        questionImageView = (ImageView) header.findViewById(R.id.imageViewQuestion);

        flashCardsListView.addHeaderView(header, null, false);

        if (mFlashCard.question_img != null) {
            if (mFlashCard.question_img.text != null)
                questionTextView.setText(mFlashCard.question_img.text);
            if ((mFlashCard.question_img.image != null)
                    && (mFlashCard.question_img.image.url != null)) {
                ArrayList<Integer> size = Utils.scaleToMaxXY(mFlashCard.question_img.image.width,
                        mFlashCard.question_img.image.height);
                Integer width = size.get(0);
                Integer height = size.get(1);
                ViewGroup.LayoutParams params = questionImageView.getLayoutParams();
                params.width = width;
                params.height = height;
                questionImageView.setLayoutParams(params);
                Multicards.getAvatarLoader().get(mFlashCard.question_img.image.url,
                        new Utils.AvatarListener(questionImageView));
                questionImageView.setVisibility(View.VISIBLE);
            } else
                questionImageView.setVisibility(View.GONE);

        }
        listViewAdapter.setValues(mFlashCard.options_img);
        flashCardsListView.setAdapter(listViewAdapter);
        flashCardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                position -= flashCardsListView.getHeaderViewsCount();
                onAnswerPick.onResponse(position);
            }
        });

        if (intActionType == INT_SHOW_ANSWER) {
            listViewAdapter.setCorrectAnswer(mFlashCard.answer_id);
            listViewAdapter.setWrongAnswer(mFlashCard.wrong_answer_id);
        }

        Multicards.getMainActivity().settingsImageView.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputDialogInterface.showFlashcardSettingsDialog(new CallbackInterface() {
                            @Override
                            public void onResponse(Object obj) {
                                Log.d(LOG_TAG, "Cardset is inverting");
                                Log.d(LOG_TAG, "Cardset is inverting 1 " + GameplayManager.currentGameplay.strGID);
                                String strGID = GameplayManager.currentGameplay.strGID;
                                if ((strGID != null) && (!strGID.isEmpty())) {
                                    ArrayList<String> flags = new ArrayList<String>();
                                    flags.add(Constants.FLAG_CARDSET_INVERTED.toString());
                                    ServerInterface.flagCardsetRequest(strGID, flags, null, null);
                                    Cardset cardset = Multicards.getFlashCardsDAO().
                                            fetchCardset(strGID, false);
                                    Log.d(LOG_TAG, "Cardset is " + (cardset.inverted ? "inverted" : "not inverted"));
                                    cardset.inverted = !cardset.inverted;
                                    cardset.save();
                                }
                            }
                        });
                    }
                });

        return true;
    }

    public void setOnAnswerPickListener (final CallbackInterface onAnswerPick) {
        this.onAnswerPick = onAnswerPick;
    }

    public void setEmptyOnFlashcardItemClickListener() {
        flashCardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim)
    {

        Log.d(LOG_TAG, "onCreateAnimator" + nextAnim);
        try {
            final Animator anim = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    onFlashCardItemClickListener = flashCardsListView.getOnItemClickListener();
                    setEmptyOnFlashcardItemClickListener();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    flashCardsListView.setOnItemClickListener(onFlashCardItemClickListener);
                }
            });
            return anim;
        } catch (Exception e ) {
            return super.onCreateAnimator(transit, enter, nextAnim);
        }
    }

    public static FlashCardFragment cloneFragment (FlashCardFragment fragment) {
        FlashCardFragment newFragment = new FlashCardFragment();
        newFragment.mFlashCard = fragment.getFlashCard();
        newFragment.onAnswerPick = fragment.onAnswerPick;
        newFragment.intActionType = fragment.intActionType;
        newFragment.view = fragment.view;
        return newFragment;
    }
}
