package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;

/**
 * Created by alex-mac on 31.01.16.
 */
public class AnswerLogAdapter extends RecyclerView.Adapter<AnswerLogAdapter.ViewHolder> {
    private Context context;
    private GameplayData gameplayData;
    private int intCount = 0;
    private static int intStartDelay = 0;
    private static CallbackInterface onAddItem;

    public AnswerLogAdapter(Context context, GameplayData gameplayData) {
        this.context = context;
        this.gameplayData = gameplayData;
    }

    public void setAddItemCallback (CallbackInterface onAddItem) {
        this.onAddItem = onAddItem;
    }

    public void initStartDelay () {
        intStartDelay = 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.answer_log_view, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.intStartDelay = intStartDelay;
        intStartDelay = intStartDelay + 500;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FlashCard question = gameplayData.questions.get(position);
        String strText = "";
        if (gameplayData.answers.get(position) != -1)
            strText = question.question + " - " +
                question.options.get(gameplayData.answers.get(position));
        else
            strText = question.question + " - X";
        holder.textViewQuestion.setText(strText);
        if (gameplayData.checks.get(position)) {
            holder.imageViewCorrect.setVisibility(View.VISIBLE);
            holder.imageViewWrong.setVisibility(View.GONE);
        } else {
            holder.imageViewCorrect.setVisibility(View.GONE);
            holder.imageViewWrong.setVisibility(View.VISIBLE);
        }
        holder.position = position;
        holder.onClickListener = new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                FlashCard question = gameplayData.questions.get(holder.position);
                String strText = question.question + ": \n" +
                        question.options.get(question.answer_id);
                InputDialogInterface.showModalDialog(strText, Multicards.getGameOverActivity());
            }
        };
    }

    @Override
    public int getItemCount() {
        return intCount;
    }

    public void addItem() {
        intCount++;
        notifyItemInserted(intCount-1);
    }

    static class ViewHolder extends AnimateViewHolder
            implements View.OnClickListener{

        TextView textViewQuestion;
        ImageView imageViewCorrect;
        ImageView imageViewWrong;
        int intStartDelay = 0;
        int position = -1;
        CallbackInterface onClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewQuestion = (TextView) itemView.findViewById(R.id.textViewQuestion);
            imageViewCorrect = (ImageView) itemView.findViewById(R.id.imageViewCorrect);
            imageViewWrong = (ImageView) itemView.findViewById(R.id.imageViewWrong);
        }

        @Override
        public void animateRemoveImpl(ViewPropertyAnimatorListener listener) {
        }

        @Override
        public void preAnimateAddImpl() {
            ViewCompat.setTranslationX(itemView, -itemView.getWidth() * 0.3f);
            ViewCompat.setAlpha(itemView, 0);
        }

        @Override
        public void animateAddImpl(final ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView)
                    .translationX(0)
                    .alpha(1)
                    .setDuration(300)
                    .setStartDelay(intStartDelay)
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                            listener.onAnimationStart(view);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            listener.onAnimationEnd(view);
                            if (onAddItem != null)
                                onAddItem.onResponse(position);
                        }

                        @Override
                        public void onAnimationCancel(View view) {
                            listener.onAnimationCancel(view);
                        }
                    })
                    .start();
        }

        @Override
        public void onClick(View view) {
            if (onClickListener != null)
                onClickListener.onResponse(null);
        }
    }
}
