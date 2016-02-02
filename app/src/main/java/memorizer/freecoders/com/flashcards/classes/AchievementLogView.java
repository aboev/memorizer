package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import memorizer.freecoders.com.flashcards.R;

/**
 * Created by alex-mac on 16.01.16.
 */
public class AchievementLogView extends LinearLayout {

    private TextView textViewAchievement;
    private ImageView imageViewAchievement;
    private View view;

    public AchievementLogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public AchievementLogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AchievementLogView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        view = inflate(getContext(), R.layout.answer_log_view, null);
        textViewAchievement = (TextView) view.findViewById(R.id.textViewAchievement);
        imageViewAchievement = (ImageView) view.findViewById(R.id.imageViewAchievement);
        addView(view);
    }

    public void setText (String strText) {
        textViewAchievement.setText(strText);
        invalidate();
    }

}