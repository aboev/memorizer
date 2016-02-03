package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.BonusDescriptor;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 31.01.16.
 */
public class AchievementLogAdapter extends RecyclerView.Adapter<AchievementLogAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BonusDescriptor> values = new ArrayList<BonusDescriptor>();
    private int intCount = 0;
    private static int intStartDelay = 0;
    private static CallbackInterface onAddItem;

    public AchievementLogAdapter(Context context) {
        this.context = context;
        this.values = values;
    }

    public void setValues (ArrayList<BonusDescriptor> values) {
        this.values = values;
    }

    public void setAddItemCallback (CallbackInterface onAddItem) {
        this.onAddItem = onAddItem;
    }

    public void initStartDelay () {
        intStartDelay = 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.achievement_log_view, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.intStartDelay = intStartDelay;
        intStartDelay = intStartDelay + 500;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        BonusDescriptor bonusDescriptor = values.get(position);
        String strText = "";

        if (bonusDescriptor.bonus_title != null)
            if (bonusDescriptor.bonus_title.containsKey(Utils.getLocale()))
                strText = bonusDescriptor.bonus_title.get(Utils.getLocale());
            else if (bonusDescriptor.bonus_title.containsKey(Constants.DEFAULT_LOCALE))
                strText = bonusDescriptor.bonus_title.get(Constants.DEFAULT_LOCALE);

        strText = strText + " (" + bonusDescriptor.bonus + ")";


        holder.textViewAchievement.setText(strText);

        holder.position = position;
        holder.onClickListener = new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
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

        TextView textViewAchievement;
        ImageView imageViewAchievement;
        int intStartDelay = 0;
        int position = -1;
        CallbackInterface onClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewAchievement = (TextView) itemView.findViewById(R.id.textViewAchievement);
            imageViewAchievement = (ImageView) itemView.findViewById(R.id.imageViewAchievement);
        }

        @Override
        public void animateRemoveImpl(ViewPropertyAnimatorListener listener) {
        }

        @Override
        public void preAnimateAddImpl() {
            ViewCompat.setTranslationY(itemView, -itemView.getHeight() * 0.3f);
            ViewCompat.setAlpha(itemView, 0);
        }

        @Override
        public void animateAddImpl(final ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView)
                    .translationY(0)
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
