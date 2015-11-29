package memorizer.freecoders.com.flashcards.common;

import android.app.Application;

import memorizer.freecoders.com.flashcards.MainActivity;
import memorizer.freecoders.com.flashcards.MultiplayerInterface;
import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 08.11.15.
 */
public class MemorizerApplication extends Application {
    private static MainActivity mMainActivity;
    private static FlashCardsDAO mFlashCardsDAO;
    private static ServerInterface mServerInterface;
    private static Preferences mPreferences;
    private static MultiplayerInterface mMultiplayerInterface;

    @Override
    public void onCreate() {
        super.onCreate();

        mPreferences = new Preferences(this);
        mPreferences.loadPreferences();

        mServerInterface = new ServerInterface();
    }

    public final static void setFlashCardActivity(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public final static MainActivity getFlashCardActivity(){
        return mMainActivity;
    }

    public final static void setFlashCardsDAO(FlashCardsDAO flashCardsDAO) {
        mFlashCardsDAO = flashCardsDAO;
    }

    public final static FlashCardsDAO getFlashCardsDAO (){
        return mFlashCardsDAO;
    }

    public final static void setServerInterface (ServerInterface serverInterface) {
        mServerInterface = serverInterface;
    }

    public final static ServerInterface getServerInterface () {
        return mServerInterface;
    }

    public final static Preferences getPreferences () {
        return mPreferences;
    }

    public final static void setMultiPlayerInterface (MultiplayerInterface multiPlayerInterface) {
        mMultiplayerInterface = multiPlayerInterface;
    }

    public final static MultiplayerInterface getMultiplayerInterface () {
        return mMultiplayerInterface;
    }

}
