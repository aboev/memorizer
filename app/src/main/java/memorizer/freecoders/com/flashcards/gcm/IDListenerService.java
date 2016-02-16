package memorizer.freecoders.com.flashcards.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by alex-mac on 13.02.16.
 */
public class IDListenerService extends InstanceIDListenerService {

    private static final String TAG = "IDListenerService";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
