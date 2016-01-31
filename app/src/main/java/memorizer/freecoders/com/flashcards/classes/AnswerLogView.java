package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.TagDescriptor;

/**
 * Created by alex-mac on 16.01.16.
 */
public class AnswerLogView extends LinearLayout {

    private TextView textViewQuestion;
    private ImageView imageViewCorrect;
    private ImageView imageViewWrong;
    private View view;

    public AnswerLogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public AnswerLogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AnswerLogView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        view = inflate(getContext(), R.layout.answer_log_view, null);
        textViewQuestion = (TextView) view.findViewById(R.id.textViewQuestion);
        imageViewCorrect = (ImageView) view.findViewById(R.id.imageViewCorrect);
        imageViewWrong = (ImageView) view.findViewById(R.id.imageViewWrong);
        addView(view);
    }

    public void setText (String strText) {
        textViewQuestion.setText(strText);
        invalidate();
    }

    public void setCorrect (Boolean boolCorrect) {
        if (boolCorrect) {
            imageViewCorrect.setVisibility(VISIBLE);
            imageViewWrong.setVisibility(GONE);
        } else {
            imageViewCorrect.setVisibility(GONE);
            imageViewWrong.setVisibility(VISIBLE);
        }
    }
}