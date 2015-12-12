package memorizer.freecoders.com.flashcards;

import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
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
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.android.volley.Response;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;

import java.net.URISyntaxException;

import io.socket.client.Socket;
import io.socket.client.IO;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.classes.StyleProgressBar;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;
import memorizer.freecoders.com.flashcards.json.Question;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivity";

    public int intUIState;

    public Fragment currentFragment;
    public FlashCardFragment currentFlashCardFragment;
    public PlayersInfoFragment playersInfoFragment = new PlayersInfoFragment();

    TextView scoreView;

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

            showNextFragment(new MainMenuFragment(), null);

        }

    }

    public void nextFlashCard(){
        FlashCardFragment newFlashCardFragment = new FlashCardFragment();
        showNextFragment(newFlashCardFragment, null);
    }

    public void nextFlashCard(Question question){
        FlashCardFragment newFlashCardFragment = new FlashCardFragment();
        newFlashCardFragment.setActionType(FlashCardFragment.INT_GIVEN_FLASHCARD);
        FlashCard flashCard = new FlashCard();
        flashCard.question = question.question;
        flashCard.options = question.options;
        flashCard.answer_id = question.answer_id;
        newFlashCardFragment.setFlashCard(flashCard);
        showNextFragment(newFlashCardFragment, null);
    }

    public void showAnswer(int intWrongAnswerID){
        FlashCardFragment newFlashCardFragment = new FlashCardFragment();
        newFlashCardFragment.setActionType(FlashCardFragment.INT_SHOW_ANSWER);
        currentFlashCardFragment.getFlashCard().wrong_answer_id = intWrongAnswerID;
        newFlashCardFragment.setFlashCard(currentFlashCardFragment.getFlashCard());
        showNextFragment(newFlashCardFragment, FragmentTransactionExtended.FLIP_HORIZONTAL);
    }

    public void showNextFragment (Fragment newFragment, Integer intTransitionType) {
        if (currentFragment == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_flashcard_container, newFragment).commit();
        } else {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            FragmentTransactionExtended fragmentTransactionExtended =
                    new FragmentTransactionExtended(this, fragmentTransaction, currentFragment,
                            newFragment, R.id.fragment_flashcard_container);
            if (intTransitionType == null)
                fragmentTransactionExtended.addTransition(FragmentTransactionExtended.SLIDE_HORIZONTAL);
            else
                fragmentTransactionExtended.addTransition(intTransitionType);
            fragmentTransactionExtended.commit();
        }

        currentFragment = newFragment;

        if (newFragment instanceof FlashCardFragment)
            currentFlashCardFragment = (FlashCardFragment) newFragment;
    }

    public void showPlayersInfo () {
        getFragmentManager().beginTransaction()
                    .add(R.id.fragment_players_info_container, playersInfoFragment).commit();
    }

    public void initApp(){
        MemorizerApplication.setFlashCardsDAO(new FlashCardsDAO(this));
        MemorizerApplication.setMainActivity(this);

        MemorizerApplication.setServerInterface(new ServerInterface());

        if ((MemorizerApplication.getPreferences().strUserID == null) ||
                MemorizerApplication.getPreferences().strUserID.isEmpty()) {
            ServerInterface.registerUserRequest(
                    new UserDetails(),
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse(String response) {
                            MemorizerApplication.getPreferences().strUserID = response;
                            MemorizerApplication.getPreferences().savePreferences();
                        }
                    }, null);
        }

        try {
            Socket mSocket = IO.socket(Constants.SOCKET_SERVER_URL);
            mSocket.on(Constants.SOCKET_CHANNEL_NAME,
                    MemorizerApplication.getServerInterface().onNewSocketMessage);
            mSocket.connect();
            MemorizerApplication.getServerInterface().setSocketIO(mSocket);
            Log.d(LOG_TAG, "Connected to socket");
        } catch (URISyntaxException e) {
            Log.d(LOG_TAG, "Failed to connect to socket");
        }
    }

    public void returnToMainMenu () {
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_flashcard_container,
                mainMenuFragment).commit();
        getFragmentManager().beginTransaction().remove(MemorizerApplication.getMainActivity().
                playersInfoFragment).commit();
        getFragmentManager().beginTransaction().remove(MemorizerApplication.getMainActivity().
                currentFlashCardFragment).commit();
        MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MemorizerApplication.getServerInterface().getSocketIO().disconnect();
        MemorizerApplication.getServerInterface().getSocketIO().off(Constants.SOCKET_CHANNEL_NAME,
                MemorizerApplication.getServerInterface().onNewSocketMessage);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (MemorizerApplication.getMainActivity().intUIState == Constants.UI_STATE_MULTIPLAYER_MODE)
                MemorizerApplication.getMultiplayerInterface().quitGame();

            if (MemorizerApplication.getMainActivity().intUIState != Constants.UI_STATE_MAIN_MENU )
                returnToMainMenu();
            else
                finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
