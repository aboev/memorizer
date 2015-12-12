package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.animations.ResizeWidthAnimation;
import memorizer.freecoders.com.flashcards.common.Animations;

/**
 * Created by alex-mac on 12.12.15.
 */
public class StyleProgressBar extends FrameLayout {
    private static String LOG_TAG = "StyleProgressBar";

    private View backgroundView;
    private View foreroundView;

    private int progress;

    public StyleProgressBar(Context context) {
        super(context);
        init();
    }

    public StyleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StyleProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.progressbar, this);
        this.foreroundView = (View) findViewById(R.id.progressbar_foreground);
        this.backgroundView = (View) findViewById(R.id.progressbar_background);
    }

    public void setProgress(int intProgress, Boolean boolAnimate) {
        if (intProgress < 0) intProgress = 0;
        if (intProgress > 100) intProgress = 100;

        progress = intProgress;
        int back_width = backgroundView.getMeasuredWidth();
        int fore_width = back_width * intProgress / 100;
        if (boolAnimate) {
            ResizeWidthAnimation anim = new ResizeWidthAnimation(foreroundView, fore_width);
            anim.setDuration(500);
            backgroundView.startAnimation(anim);
        } else {
            ViewGroup.LayoutParams params = foreroundView.getLayoutParams();
            params.width = fore_width;
            foreroundView.setLayoutParams(params);
        }
    }

    public int getProgress () {
        return progress;
    }

}
