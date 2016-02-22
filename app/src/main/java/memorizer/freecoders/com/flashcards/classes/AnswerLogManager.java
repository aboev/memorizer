package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;

/**
 * Created by alex-mac on 17.02.16.
 */
public class AnswerLogManager {

    private static String LOG_TAG = "AnswerLogManager";

    GameplayData gameplayData;
    LinearLayout parentView;
    Context context;
    CallbackInterface onPopulateCorrectAnswer;
    CallbackInterface onFinalAnimation;

    public AnswerLogManager(Context context, GameplayData gameplayData, LinearLayout parentView) {
        this.context = context;
        this.gameplayData = gameplayData;
        this.parentView = parentView;
    }

    public void setAnimationCallback (CallbackInterface callback) {
        this.onPopulateCorrectAnswer = callback;
    }

    public void setFinalAnimationCallback (CallbackInterface callback) {
        this.onFinalAnimation = callback;
    }

    public void populateView (Boolean enableAnimation) {
        int animOffset = 100;
        int animDuration = 500;
        for (int i = 0; i < gameplayData.questions.size(); i++) {
            AnswerLogView answer = new AnswerLogView(context);
            final FlashCard question = gameplayData.questions.get(i);
            String strText = "";
            if (gameplayData.answers.get(i) != -1)
                strText = question.question + " - " +
                        question.options.get(gameplayData.answers.get(i));
            else
                strText = question.question + " - X";
            answer.setText(strText);
            answer.setCorrect(gameplayData.checks.get(i));

            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strText = question.question + ": \n" +
                            question.options.get(question.answer_id);
                    InputDialogInterface.showModalDialog(strText, Multicards.getGameOverActivity());
                }
            });

            parentView.addView(answer);

            if ((enableAnimation) && (gameplayData.checks.get(i)))
                Animations.customAnimation(answer, R.anim.slide_in_left_fade_in,
                    animDuration, animOffset * i , onPopulateCorrectAnswer);

            if ((enableAnimation) && (i == gameplayData.questions.size() - 1))
                onFinalAnimation.onResponse(null);
        }
    }

}
