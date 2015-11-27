package memorizer.freecoders.com.flashcards;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.common.Preferences;
import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivity";

    private FlashCardFragment currentFlashCardFragment;
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
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if ( savedInstanceState != null) {
                return;
            }

            ActiveAndroid.initialize(this);
            initApp();

            // Create a new Fragment to be placed in the activity layout
            currentFlashCardFragment = new FlashCardFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            currentFlashCardFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, currentFlashCardFragment).commit();
        }

        scoreView = (TextView) findViewById(R.id.scoreView);
        updateScore(0,0);
    }

    public void updateScore(int numCorrect,int numTotal)
    {
        scoreView.setText("Score: "+ Integer.toString(numCorrect)+"/"+Integer.toString(numTotal));
    }

    public void nextFlashCard(){
        FlashCardFragment newFlashCardFragment = new FlashCardFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        FragmentTransactionExtended fragmentTransactionExtended =
                new FragmentTransactionExtended(this, fragmentTransaction, currentFlashCardFragment,
                newFlashCardFragment, R.id.fragment_container);
        fragmentTransactionExtended.addTransition(FragmentTransactionExtended.SLIDE_HORIZONTAL);

        fragmentTransactionExtended.commit();
        currentFlashCardFragment = newFlashCardFragment;
    }

    public void showAnswer(int intWrongAnswerID){
        FlashCardFragment newFlashCardFragment = new FlashCardFragment();
        newFlashCardFragment.setActionType(FlashCardFragment.INT_SHOW_ANSWER);
        currentFlashCardFragment.getFlashCard().wrong_answer_id = intWrongAnswerID;
        newFlashCardFragment.setFlashCard(currentFlashCardFragment.getFlashCard());
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        FragmentTransactionExtended fragmentTransactionExtended =
                new FragmentTransactionExtended(this, fragmentTransaction, currentFlashCardFragment,
                        newFlashCardFragment, R.id.fragment_container);
        fragmentTransactionExtended.addTransition(FragmentTransactionExtended.FLIP_HORIZONTAL);

        fragmentTransactionExtended.commit();
        currentFlashCardFragment = newFlashCardFragment;
    }

    public void initApp(){
        MemorizerApplication.setFlashCardsDAO(new FlashCardsDAO(this));
        MemorizerApplication.setFlashCardActivity(this);

        MemorizerApplication.setServerInterface(new ServerInterface());

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

}
