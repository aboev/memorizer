package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import memorizer.freecoders.com.flashcards.classes.AutoResizeTextView;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.classes.ListViewAdapter;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;

/**
 * Created by alex-mac on 07.11.15.
 */
public class FlashCardFragment extends Fragment {

    public final static int INT_SHOW_ANSWER = 0;   // Show user mistake and correct answer
    public final static int INT_NEW_FLASHCARD = 1; // Show new flashcard (default)
    public final static int INT_GIVEN_FLASHCARD = 2; // Show server flashcard

    public static int numCorrectAnswers=0;
    public static int numTotalAnswers=0;

    private View view;

    private FlashCard mFlashCard;
    ListView flashCardsListView;
    ListViewAdapter listViewAdapter;
    AutoResizeTextView questionTextView;
    private int intActionType = INT_NEW_FLASHCARD;


    public void setActionType(int intActionType) {
        this.intActionType = intActionType;
    }

    public void setFlashCard(FlashCard flashCard) {
        this.mFlashCard = flashCard;
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



    public boolean populateView() {
        flashCardsListView = (ListView) view.findViewById(R.id.ListView_FlashCard);
        questionTextView = (AutoResizeTextView) view.findViewById(R.id.TextView_Question);
        listViewAdapter = new ListViewAdapter(view.getContext());
        //MemorizerApplication.getFlashCardActivity().updateScore();

        if (intActionType == INT_NEW_FLASHCARD) {
            mFlashCard = MemorizerApplication.getFlashCardsDAO().fetchRandomCard();
            questionTextView.setText(mFlashCard.question);
            listViewAdapter.setValues(mFlashCard.options);

            flashCardsListView.setAdapter(listViewAdapter);

            flashCardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    numTotalAnswers++;
                    if (mFlashCard.answer_id == position) {
                        listViewAdapter.setCorrectAnswer(mFlashCard.answer_id);
                        listViewAdapter.notifyDataSetChanged();
                        numCorrectAnswers++;
                        MemorizerApplication.getFlashCardActivity().updateScore(numCorrectAnswers,numTotalAnswers);

                        MemorizerApplication.getFlashCardActivity().nextFlashCard();
                    } else {
                        //Toast.makeText(view.getContext(),"Wrong! Try again", Toast.LENGTH_LONG).show();
                        MemorizerApplication.getFlashCardActivity().updateScore(numCorrectAnswers,numTotalAnswers);
                        wrongAnswerNotify();
                        MemorizerApplication.getFlashCardActivity().showAnswer(position);
                    }
                }
            });
        } else if (intActionType == INT_SHOW_ANSWER) {
            questionTextView.setText(mFlashCard.question);
            listViewAdapter.setValues(mFlashCard.options);
            listViewAdapter.setCorrectAnswer(mFlashCard.answer_id);
            listViewAdapter.setWrongAnswer(mFlashCard.wrong_answer_id);

            flashCardsListView.setAdapter(listViewAdapter);

            flashCardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (mFlashCard.answer_id == position) {
                        MemorizerApplication.getFlashCardActivity().nextFlashCard();
                    } else
                        wrongAnswerNotify();
                }
            });

        } else if (intActionType == INT_GIVEN_FLASHCARD) {
            questionTextView.setText(mFlashCard.question);
            listViewAdapter.setValues(mFlashCard.options);

            flashCardsListView.setAdapter(listViewAdapter);

            flashCardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (mFlashCard.answer_id == position) {
                        MemorizerApplication.getFlashCardActivity().nextFlashCard();
                    } else
                        wrongAnswerNotify();
                }
            });
        }

        return true;
    }
}
