package memorizer.freecoders.com.flashcards;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;

/**
 * Created by alex-mac on 08.11.15.
 */
public class MemorizerApplication extends Application {
    private static FlashCardActivity mFlashCardActivity;
    private static FlashCardsDAO mFlashCardsDAO;

    public final static void setFlashCardActivity(FlashCardActivity flashCardActivity) {
        mFlashCardActivity = flashCardActivity;
    }

    public final static FlashCardActivity getFlashCardActivity(){
        return mFlashCardActivity;
    }

    public final static void setFlashCardsDAO(FlashCardsDAO flashCardsDAO) {
        mFlashCardsDAO = flashCardsDAO;
    }

    public final static FlashCardsDAO getFlashCardsDAO (){
        return mFlashCardsDAO;
    }

}
