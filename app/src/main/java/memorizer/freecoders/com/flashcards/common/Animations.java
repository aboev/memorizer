package memorizer.freecoders.com.flashcards.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

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

    public final static void alphaAnimation (final View view){

        final int visibility = view.getVisibility();
        int duration = 500;

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);

        final AlphaAnimation reverse_animation = new AlphaAnimation(1.0f, 0.0f);
        reverse_animation.setDuration(duration);
        reverse_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(visibility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(reverse_animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
    }

    public final static void scaleAnimation (final View view){
        float scale = 1.2f;

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, scale, 1.0f, scale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        int duration = 500;

        final ScaleAnimation reverse_animation = new ScaleAnimation(scale, 1.0f, scale, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        reverse_animation.setDuration(duration);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(reverse_animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(scaleAnimation);


    }
}
