package memorizer.freecoders.com.flashcards.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import memorizer.freecoders.com.flashcards.classes.CallbackInterface;

/**
 * Created by alex-mac on 02.12.15.
 */
public class Animations {

    private static String LOG_TAG = "Animations";

    public final static void highlightColor (View view,
            int colorStart, int colorEnd, final CallbackInterface onAnimationEnd) {


        int duration = 1000;
        AnimatorSet mAnimationSet = new AnimatorSet();
        ObjectAnimator fadeIn = ObjectAnimator.ofObject(view, "backgroundColor",
                new ArgbEvaluator(), colorStart, colorEnd);
        fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onAnimationEnd.onResponse(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        fadeIn.start();
    }
}
