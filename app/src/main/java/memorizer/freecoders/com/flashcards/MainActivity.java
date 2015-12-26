package memorizer.freecoders.com.flashcards;


import android.app.Fragment;
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
import java.net.URISyntaxException;

import io.socket.client.Socket;
import io.socket.client.IO;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;
import memorizer.freecoders.com.flashcards.json.Question;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivity";

    public int intUIState;

    public Fragment currentFragment;
    public FlashCardFragment currentFlashCardFragment;
    public MainMenuFragment mainMenuFragment;
    public SearchCardsetFragment cardsetPickerFragment;
    public PlayersInfoFragment playersInfoFragment = new PlayersInfoFragment();

    TextView scoreView;
    private Long SetID = null;

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

            mainMenuFragment = new MainMenuFragment();
            showNextFragment(mainMenuFragment, null);

        }

    }

    public void setSetID (Long setID) {
        this.SetID = setID;
    }

    public Long getSetID () {
        return this.SetID;
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
        showNextFragment(newFlashCardFragment, Constants.ANIMATION_FLIP);
    }

    public void showNextFragment (Fragment newFragment, Integer intTransitionType) {
            if (intTransitionType == null)
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                                R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_flashcard_container, newFragment)
                        .commit();
            else if (intTransitionType == Constants.ANIMATION_FLIP)
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.flip_right_in, R.anim.flip_right_out,
                                R.anim.flip_left_in, R.anim.flip_left_out)
                        .replace(R.id.fragment_flashcard_container, newFragment)
                        .commit();

        currentFragment = newFragment;

        if (newFragment instanceof FlashCardFragment)
            currentFlashCardFragment = (FlashCardFragment) newFragment;
    }

    public void showPlayersInfo () {
        getFragmentManager().beginTransaction()
                    .add(R.id.fragment_players_info_container, playersInfoFragment).commit();
    }

    public void initApp(){
        Multicards.setFlashCardsDAO(new FlashCardsDAO(this));
        Multicards.setMainActivity(this);

        Multicards.setServerInterface(new ServerInterface());

        try {
            Socket mSocket = IO.socket(Constants.SOCKET_SERVER_URL);
            mSocket.on(Constants.SOCKET_CHANNEL_NAME,
                    Multicards.getServerInterface().onNewSocketMessage);
            mSocket.connect();
            Multicards.getServerInterface().setSocketIO(mSocket);
            Multicards.getPreferences().strSocketID = mSocket.id();
            Log.d(LOG_TAG, "Connected to socket");
        } catch (URISyntaxException e) {
            Log.d(LOG_TAG, "Failed to connect to socket");
        }

        if ((Multicards.getPreferences().strUserID == null) ||
                Multicards.getPreferences().strUserID.isEmpty()) {
            ServerInterface.registerUserRequest(
                    new UserDetails(),
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse(String response) {
                            Multicards.getPreferences().strUserID = response;
                            Multicards.getPreferences().savePreferences();
                        }
                    }, null);
        } else
            ServerInterface.socketAnnounceUserID(Multicards.getPreferences().strUserID);
    }

    public void returnToMainMenu () {
        mainMenuFragment = new MainMenuFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_flashcard_container,
                mainMenuFragment).commit();
        if (Multicards.getMainActivity().playersInfoFragment != null)
            getFragmentManager().beginTransaction().remove(Multicards.getMainActivity().
                playersInfoFragment).commit();
        if (Multicards.getMainActivity().currentFlashCardFragment != null)
            getFragmentManager().beginTransaction().remove(Multicards.getMainActivity().
                currentFlashCardFragment).commit();
        if (Multicards.getMainActivity().cardsetPickerFragment != null)
            getSupportFragmentManager().beginTransaction().remove(Multicards.getMainActivity().
                    cardsetPickerFragment).commit();
        Multicards.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Multicards.getServerInterface().getSocketIO().disconnect();
        Multicards.getServerInterface().getSocketIO().off(Constants.SOCKET_CHANNEL_NAME,
                Multicards.getServerInterface().onNewSocketMessage);
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
