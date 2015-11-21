package memorizer.freecoders.com.flashcards.common;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.github.nkzawa.socketio.client.Socket;

import memorizer.freecoders.com.flashcards.FlashCardActivity;
import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 08.11.15.
 */
public class MemorizerApplication extends Application {
    private static FlashCardActivity mFlashCardActivity;
    private static FlashCardsDAO mFlashCardsDAO;
    private static Socket mSocketIO;
    private static ServerInterface mServerInterface;

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

}
