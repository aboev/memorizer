package memorizer.freecoders.com.flashcards;


import android.content.Intent;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.socket.client.Socket;
import io.socket.client.IO;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.FlashCardsDAO;
import memorizer.freecoders.com.flashcards.gcm.RegistrationIntentService;
import memorizer.freecoders.com.flashcards.json.InvitationDescriptor;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.network.SocketInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public TextView textViewNetworkState;
    public LinearLayout linearLayoutEmoticons;
    public ImageView imageViewEmoticon1;
    public ImageView imageViewEmoticon2;
    public ImageView imageViewEmoticon3;
    public ImageView imageViewEmoticon4;
    public ImageView imageViewEmoticon5;
    public ImageView imageViewEmoticon6;

    private static String LOG_TAG = "MainActivity";

    public Boolean boolIsForeground = true;

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

        textViewNetworkState = (TextView) findViewById(R.id.textViewNetworkState);

        linearLayoutEmoticons = (LinearLayout) findViewById(R.id.linearLayoutEmoticons);

        imageViewEmoticon1 = (ImageView) findViewById(R.id.imageViewEmoticon1);
        imageViewEmoticon2 = (ImageView) findViewById(R.id.imageViewEmoticon2);
        imageViewEmoticon3 = (ImageView) findViewById(R.id.imageViewEmoticon3);
        imageViewEmoticon4 = (ImageView) findViewById(R.id.imageViewEmoticon4);
        imageViewEmoticon5 = (ImageView) findViewById(R.id.imageViewEmoticon5);
        imageViewEmoticon6 = (ImageView) findViewById(R.id.imageViewEmoticon6);

        Log.d(LOG_TAG, "OnCreate " + (savedInstanceState == null ? "savedInstanceState == null" :
                "savedInstanceState != null"));
        populateView(savedInstanceState);

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        Intent intent = getIntent();
        onNewIntent(intent);
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
        Log.d(LOG_TAG, "populateView");
        if (findViewById(R.id.fragment_flashcard_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            Log.d(LOG_TAG, "Populating view " + (savedInstanceState != null ? "restoring" : ""));
            if ( savedInstanceState == null) {
                ActiveAndroid.initialize(this);
                initApp(false);
                FragmentManager.showMainMenu(false);
            } else {
                initApp(true);
                restoreApp();
            }
        }

    }

    public void initApp(Boolean boolConfigurationChange){
        Multicards.setMainActivity(this);

        if (boolConfigurationChange)
            return;

        Multicards.setFlashCardsDAO(new FlashCardsDAO(this));

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
            final UserDetails userDetails = new UserDetails();
            userDetails.locale = Utils.getLocale();
            ServerInterface.registerUserRequest(
                    new UserDetails(),
                    new Response.Listener<UserDetails>() {
                        @Override
                        public void onResponse(UserDetails response) {
                            Multicards.getPreferences().strUserID = response.id;
                            Multicards.getPreferences().strUserName = response.name;
                            Multicards.getPreferences().strAvatar = response.avatar;
                            Multicards.getPreferences().savePreferences();
                            Utils.refreshPushID();
                        }
                    }, null);
        } else {
            SocketInterface.socketAnnounceUserID(Multicards.getPreferences().strUserID);
            networkRequests();
            Utils.refreshPushID();
        }

        Utils.checkLatestVersion();
    }

    public void restoreApp () {
        Log.d(LOG_TAG, "Restoring state " + FragmentManager.intUIState);
        if (FragmentManager.intUIState == Constants.UI_STATE_MAIN_MENU)
            FragmentManager.returnToMainMenu(true);
        else if (FragmentManager.intUIState == Constants.UI_STATE_TRAIN_MODE)
            FragmentManager.showGamePlayFragments(true, Constants.UI_STATE_TRAIN_MODE);
        else if (FragmentManager.intUIState == Constants.UI_STATE_MULTIPLAYER_MODE)
            FragmentManager.showGamePlayFragments(true, Constants.UI_STATE_MULTIPLAYER_MODE);
        else if (FragmentManager.intUIState == Constants.UI_STATE_SETTINGS)
            FragmentManager.showUserProfileFragment(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
        if (isFinishing()) {
            SocketInterface.getSocketIO().disconnect();
            SocketInterface.getSocketIO().off(Constants.SOCKET_CHANNEL_NAME,
                    SocketInterface.onNewSocketMessage);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (FragmentManager.intUIState == Constants.UI_STATE_MULTIPLAYER_MODE)
                Multicards.getMultiplayerInterface().quitGame();

            if (FragmentManager.intUIState != Constants.UI_STATE_MAIN_MENU )
                FragmentManager.returnToMainMenu(false);
            else
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    };

    private void networkRequests () {
        ServerInterface.getTagsRequest(null, null);
        if (Multicards.getPreferences().userDetailsCache.size() > 0) {
            ArrayList<String> idList = new ArrayList<String>();
            Set set = Multicards.getPreferences().userDetailsCache.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String strID = (String) entry.getKey();
                idList.add(strID);
            }
            ServerInterface.getUserProfiles(idList, new Response.Listener<ArrayList<UserDetails>>() {
                @Override
                public void onResponse(ArrayList<UserDetails> response) {
                   for (int i = 0; i < response.size(); i++) {
                       UserDetails userDetails = response.get(i);
                       String strID = userDetails.id;
                       if (Multicards.getPreferences().userDetailsCache.containsKey(strID))
                           Multicards.getPreferences().userDetailsCache.put(strID, userDetails);
                   }
                }
            }, null);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        boolIsForeground = false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        boolIsForeground = true;
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        this.setIntent(newIntent);

        if (newIntent.hasExtra(Constants.INTENT_META_EVENT_TYPE)) {
            int intEventType = newIntent.getIntExtra(Constants.INTENT_META_EVENT_TYPE, 0);
            Log.d(LOG_TAG, "Handling event " + intEventType);
            if (intEventType == Constants.INTENT_INVITATION) {
                restoreApp();
                if (newIntent.hasExtra(Constants.INTENT_META_EVENT_BODY)) {
                    Gson gson = new Gson();
                    String strInvitation = newIntent.getStringExtra(Constants.INTENT_META_EVENT_BODY);
                    Log.d(LOG_TAG, "Received intent " + strInvitation);
                    InvitationDescriptor invitationDescriptor = gson.fromJson(strInvitation,
                            InvitationDescriptor.class);
                    GameplayManager.gameInvitation(invitationDescriptor);
                    InputDialogInterface.clearNotification(0, this);
                }
            }
        }
    }

    private boolean checkPlayServices() {
        Log.d(LOG_TAG, "checkPlayServices");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.d(LOG_TAG, "checkPlayServices failed");
            return false;
        }
        return true;
    }

}
