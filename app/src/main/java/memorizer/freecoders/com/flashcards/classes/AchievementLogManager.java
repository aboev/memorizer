package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

import javax.crypto.AEADBadTagException;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Animations;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.BonusDescriptor;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 17.02.16.
 */
public class AchievementLogManager {

    private static String LOG_TAG = "AnswerLogManager";

    ArrayList<BonusDescriptor> bonusList;
    LinearLayout parentView;
    Context context;

    public AchievementLogManager(Context context,
                                 LinearLayout parentView) {
        this.context = context;
        this.bonusList = bonusList;
        this.parentView = parentView;
    }

    public void setValues (ArrayList<BonusDescriptor> bonusList) {
        this.bonusList = bonusList;
    }

    public void populateView (final CallbackInterface onAnimationFinish,
                              final CallbackInterface onFinalAnimation) {
        int animOffset = 500;
        int animDuration = 1000;
        for (int i = 0; i < bonusList.size(); i++) {
            final AchievementLogView achievement = new AchievementLogView(context);

            String strText = "";
            final BonusDescriptor bonusDescriptor = bonusList.get(i);
            if (bonusDescriptor.bonus_title != null)
                if (bonusDescriptor.bonus_title.containsKey(Utils.getLocale()))
                    strText = bonusDescriptor.bonus_title.get(Utils.getLocale());
                else if (bonusDescriptor.bonus_title.containsKey(Constants.DEFAULT_LOCALE))
                    strText = bonusDescriptor.bonus_title.get(Constants.DEFAULT_LOCALE);

            strText = strText + " (" + bonusDescriptor.bonus + ")";

            achievement.setText(strText);

            final String strDescription;
            if (bonusDescriptor.description.containsKey(Utils.getLocale()))
                strDescription = bonusDescriptor.description.get(Utils.getLocale());
            else if (bonusDescriptor.description.containsKey(Constants.DEFAULT_LOCALE))
                strDescription = bonusDescriptor.description.get(Constants.DEFAULT_LOCALE);
            else
                strDescription = "";

            if (!strDescription.isEmpty())
                achievement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputDialogInterface.showModalDialog(strDescription,
                                Multicards.getGameOverActivity());
                    }
                });

            if ((bonusDescriptor.image_url != null) && (!bonusDescriptor.image_url.isEmpty()))
                achievement.setImageURL(bonusDescriptor.image_url);

            parentView.addView(achievement);

            final Boolean boolFinalItem = (i == (bonusList.size() - 1));
            Animations.customAnimation(achievement, R.anim.slide_down_fade_in,
                    animDuration, animOffset * i, new CallbackInterface() {
                        @Override
                        public void onResponse(Object obj) {
                            if (onAnimationFinish != null) {
                                onAnimationFinish.onResponse(bonusDescriptor.bonus);
                            }
                            if (boolFinalItem) onFinalAnimation.onResponse(null);
                        }
                    });
        }
    }

}
