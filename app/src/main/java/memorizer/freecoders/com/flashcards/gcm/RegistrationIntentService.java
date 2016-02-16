package memorizer.freecoders.com.flashcards.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 13.02.16.
 */
public class RegistrationIntentService extends IntentService {

    private static final String LOG_TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(LOG_TAG, "GCM Registration Token: " + token);
            sendRegistrationToServer(token);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to complete token refresh", e);
        }
    }

    private void sendRegistrationToServer(String token) {
        Multicards.getPreferences().strPushID = token;
        Multicards.getPreferences().boolPushIDsent = false;
        Multicards.getPreferences().savePreferences();
        Utils.refreshPushID();
    }

}