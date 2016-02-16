package memorizer.freecoders.com.flashcards.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import memorizer.freecoders.com.flashcards.MainActivity;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.InvitationDescriptor;

/**
 * Created by alex-mac on 13.02.16.
 */
public class GCMListenerService extends GcmListenerService {

    private static final String LOG_TAG = "GCMListenerService";

    private Gson gson = new Gson();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("msg");
        String event = data.getString("event");
        if ((event != null) && (event.equals(Constants.SOCK_MSG_TYPE_GAME_INVITE))) {
            try {
                InvitationDescriptor invitation =
                        gson.fromJson(message, InvitationDescriptor.class);
                sendNotification(invitation);
            }catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "Parsing exception " + e.getLocalizedMessage());
            }
        }
    }

    private void sendNotification(InvitationDescriptor invitation) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.INTENT_META_EVENT_TYPE, Constants.INTENT_INVITATION);
        intent.putExtra(Constants.INTENT_META_EVENT_BODY, gson.toJson(invitation));
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        InputDialogInterface.showInvitationNotification(invitation, pi, this);
    }
}