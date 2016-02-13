package memorizer.freecoders.com.flashcards.common;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;

import java.io.IOException;

import memorizer.freecoders.com.flashcards.CardsetPickerActivity;
import memorizer.freecoders.com.flashcards.GameOverActivity;
import memorizer.freecoders.com.flashcards.MainActivity;
import memorizer.freecoders.com.flashcards.MultiplayerInterface;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.network.VolleySingleton;
import memorizer.freecoders.com.flashcards.utils.DiskLruBitmapCache;
import memorizer.freecoders.com.flashcards.utils.MemoryLruCache;

/**
 * Created by alex-mac on 08.11.15.
 */
public class Multicards extends Application {
    private static String LOG_TAG = "Multicards";

    private static MainActivity mMainActivity;
    private static FlashCardsDAO mFlashCardsDAO;
    private static ServerInterface mServerInterface;
    private static Preferences mPreferences;
    private static MultiplayerInterface mMultiplayerInterface;
    private static CardsetPickerActivity mCardsetPickerActivity;
    private static GameOverActivity mGameOverActivity;
    private static DiskLruBitmapCache mAvatarDiskLruCache;
    private static ImageLoader mAvatarLoader;
    public static CallbackInterface onPickCardsetCallback;

    @Override
    public void onCreate() {
        super.onCreate();

        mPreferences = new Preferences(this);
        mPreferences.loadPreferences();

        mServerInterface = new ServerInterface();

        mMultiplayerInterface = new MultiplayerInterface();

        try {
            mAvatarDiskLruCache = new DiskLruBitmapCache(this, "AvatarsDiskCache",
                    2000000, Bitmap.CompressFormat.JPEG, 100);

            mAvatarLoader = new ImageLoader(VolleySingleton.getInstance(
                    this).getRequestQueue(),
                    mAvatarDiskLruCache);
        } catch (IOException e) {
            mAvatarDiskLruCache = null;
            ImageLoader.ImageCache memoryCache = new MemoryLruCache();
            mAvatarLoader = new ImageLoader(VolleySingleton.getInstance(
                    this).getRequestQueue(),
                    memoryCache);
            Log.d(LOG_TAG, "Failed to initialize disk cache");
        }
    }

    public final static void setMainActivity(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public final static MainActivity getMainActivity(){
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

    public final static void setCardsetPickerActivity(CardsetPickerActivity cardsetPickerActivity) {
        mCardsetPickerActivity = cardsetPickerActivity;
    }

    public final static CardsetPickerActivity getCardsetPickerActivity(){
        return mCardsetPickerActivity;
    }

    public final static void setGameOverActivity(GameOverActivity gameOverActivity) {
        mGameOverActivity = gameOverActivity;
    }

    public final static GameOverActivity getGameOverActivity(){
        return mGameOverActivity;
    }

    public final static ImageLoader getAvatarLoader() {return mAvatarLoader;}

}
