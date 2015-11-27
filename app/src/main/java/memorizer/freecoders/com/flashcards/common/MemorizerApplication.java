package memorizer.freecoders.com.flashcards.common;

import android.app.Application;

import com.github.nkzawa.socketio.client.Socket;

import memorizer.freecoders.com.flashcards.MainActivity;
import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 08.11.15.
 */
public class MemorizerApplication extends Application {
    private static MainActivity mMainActivity;
    private static FlashCardsDAO mFlashCardsDAO;
    private static Socket mSocketIO;
    private static ServerInterface mServerInterface;
    private static Preferences mPreferences;

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

    public final static Socket getSocketIO() {
        return mSocketIO;
    }

    public final static void setSocketIO(Socket socket){
        mSocketIO = socket;
    }

    public final static void setServerInterface (ServerInterface serverInterface) {
        mServerInterface = serverInterface;
    }

    public final static ServerInterface getServerInterface () {
        return mServerInterface;
    }

    public final static void setPreferences (Preferences preferences) {
        mPreferences = preferences;
    }

    public final static Preferences getPreferences () {
        return mPreferences;
    }

}
