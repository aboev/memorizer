package memorizer.freecoders.com.flashcards;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.ActiveAndroid;
import com.android.volley.Response;
import java.net.URISyntaxException;

import io.socket.client.Socket;
import io.socket.client.IO;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;
import memorizer.freecoders.com.flashcards.fragments.GameOverFragment;
import memorizer.freecoders.com.flashcards.fragments.MainMenuFragment;
import memorizer.freecoders.com.flashcards.fragments.PlayersInfoFragment;
import memorizer.freecoders.com.flashcards.fragments.SearchCardsetFragment;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.network.SocketInterface;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivity";

    public int intUIState;

    public MainMenuFragment mainMenuFragment;
    public SearchCardsetFragment cardsetPickerFragment;
    public GameOverFragment gameOverFragment;
    public PlayersInfoFragment playersInfoFragment = new PlayersInfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        populateView(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void populateView(Bundle savedInstanceState) {
        if (findViewById(R.id.fragment_flashcard_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if ( savedInstanceState != null) {
                return;
            }

            ActiveAndroid.initialize(this);
            initApp();

            FragmentManager.showMainMenu();
        }

    }

    public void initApp(){
        Multicards.setFlashCardsDAO(new FlashCardsDAO(this));
        Multicards.setMainActivity(this);

        Multicards.setServerInterface(new ServerInterface());

        try {
            Socket mSocket = IO.socket(Constants.SOCKET_SERVER_URL);
            mSocket.on(Constants.SOCKET_CHANNEL_NAME, SocketInterface.onNewSocketMessage);
            mSocket.on("connect", SocketInterface.onConnect);
            mSocket.connect();
            SocketInterface.setSocketIO(mSocket);
            Log.d(LOG_TAG, "Connecting to socket");
        } catch (URISyntaxException e) {
            Log.d(LOG_TAG, "Failed to connect to socket");
        }

        if ((Multicards.getPreferences().strUserID == null) ||
                Multicards.getPreferences().strUserID.isEmpty()) {
            ServerInterface.registerUserRequest(
                    new UserDetails(),
                    new Response.Listener<UserDetails> () {
                        @Override
                        public void onResponse(UserDetails response) {
                            Multicards.getPreferences().strUserID = response.id;
                            Multicards.getPreferences().strUserName = response.name;
                            Multicards.getPreferences().strAvatar = response.avatar;
                            Multicards.getPreferences().savePreferences();
                        }
                    }, null);
        } else
            SocketInterface.socketAnnounceUserID(Multicards.getPreferences().strUserID);
    }

    public void returnToMainMenu () {
        mainMenuFragment = new MainMenuFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_flashcard_container,
                mainMenuFragment).commit();
        if (FragmentManager.playersInfoFragment != null)
            getFragmentManager().beginTransaction().remove(FragmentManager.
                playersInfoFragment).commit();
        if (FragmentManager.currentFlashCardFragment != null)
            getFragmentManager().beginTransaction().
                    remove(FragmentManager.currentFlashCardFragment).commit();
        if (Multicards.getMainActivity().cardsetPickerFragment != null)
            getSupportFragmentManager().beginTransaction().remove(Multicards.getMainActivity().
                    cardsetPickerFragment).commit();
        Multicards.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketInterface.getSocketIO().disconnect();
        SocketInterface.getSocketIO().off(Constants.SOCKET_CHANNEL_NAME,
                SocketInterface.onNewSocketMessage);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_MULTIPLAYER_MODE)
                Multicards.getMultiplayerInterface().quitGame();

            if (Multicards.getMainActivity().intUIState != Constants.UI_STATE_MAIN_MENU )
                returnToMainMenu();
            else
                finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    };
}
