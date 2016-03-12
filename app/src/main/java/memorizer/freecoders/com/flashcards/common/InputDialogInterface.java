package memorizer.freecoders.com.flashcards.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.squareup.okhttp.internal.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.fragments.InvitationFragment;
import memorizer.freecoders.com.flashcards.fragments.PickGameFragment;
import memorizer.freecoders.com.flashcards.fragments.PickOpponentFragment;
import memorizer.freecoders.com.flashcards.json.InvitationDescriptor;
import memorizer.freecoders.com.flashcards.json.ServerInfo;
import memorizer.freecoders.com.flashcards.json.ServerResponse;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.utils.FileUtils;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 05.12.15.
 */
public class InputDialogInterface {

    public static InvitationFragment gameInvitationFragment;
    public static ProgressDialog progressDialog;

    public static final void askUserName (final CallbackInterface onReply) {

        Context context = Multicards.getMainActivity();
        final EditText txtUrl = new EditText(context);

        new AlertDialog.Builder(context)
                .setTitle("User profile")
                .setMessage("Please enter your name")
                .setView(txtUrl)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String strName = txtUrl.getText().toString();
                        onReply.onResponse(strName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onReply.onResponse("");
                    }
                })
                .show();
    }

    public static final void updateUserName (final CallbackInterface callbackInterface) {
        InputDialogInterface.askUserName(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                final String strName = (String) obj;
                if ((strName != null) && (!strName.isEmpty())) {
                    final UserDetails userDetails = new UserDetails();
                    userDetails.name = strName;
                    ServerInterface.updateUserDetailsRequest(userDetails,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Multicards.getPreferences().strUserName = userDetails.name;
                                    Multicards.getPreferences().savePreferences();
                                    callbackInterface.onResponse(strName);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    callbackInterface.onResponse(strName);
                                }
                            });
                }
            }
        });
    }

    public static final void askLanguage (final CallbackInterface onReply) {

        Context context = Multicards.getMainActivity();
        final EditText txtUrl = new EditText(context);

        new AlertDialog.Builder(context)
                .setTitle("Game")
                .setMessage("Please choose language")
                .setView(txtUrl)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String strName = txtUrl.getText().toString();
                        onReply.onResponse(strName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public static final void showGameStopMessage (final CallbackInterface onReply) {

        String strMessage = Multicards.getMainActivity().getResources().
                getString(R.string.string_game_stopped);

        Context context = Multicards.getMainActivity();

        new AlertDialog.Builder(context)
                .setTitle("Game")
                .setMessage(strMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();
    }

    public static final void showPickOpponentDialog (final CallbackInterface onClick) {
        String strTitle = Multicards.getMainActivity().
                getResources().getString(R.string.string_choose_opponent);
        String[] mItems = Multicards.getMainActivity().
                getResources().getStringArray(R.array.dialog_items_multiplayer);
        CharSequence colors[] = new CharSequence[] {mItems[0], mItems[1], mItems[2]};

        AlertDialog.Builder builder = new AlertDialog.Builder(Multicards.getMainActivity());
        builder.setTitle(strTitle);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onClick != null) onClick.onResponse(which);
            }
        });
        builder.show();
    }

    public static final void showMultiplayerDialog (final CallbackInterface onClick) {
        String[] mItems = Multicards.getMainActivity().
                getResources().getStringArray(R.array.dialog_multiplayer);
        CharSequence colors[] = new CharSequence[] {mItems[0], mItems[1]};

        final AlertDialog.Builder builder = new AlertDialog.Builder(Multicards.getMainActivity());
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (onClick != null) onClick.onResponse(which);
            }
        });
        builder.show();
    }

    public static final void showEnterOpponentNameDialog (final CallbackInterface onEnter) {
        android.app.FragmentManager fm =
                Multicards.getMainActivity().getFragmentManager();
        PickOpponentFragment pickOpponentFragment = new PickOpponentFragment();
        pickOpponentFragment.setOnClickOKListener(onEnter);
        pickOpponentFragment.show(fm, Constants.TAG_PICK_OPPONENT_FRAGMENT);
    }

    public static final void showChooseGameDialog (final CallbackInterface onEnter) {
        android.app.FragmentManager fm =
                Multicards.getMainActivity().getFragmentManager();
        PickGameFragment pickGameFragment = new PickGameFragment();
        pickGameFragment.setOnClickOKListener(onEnter);
        pickGameFragment.show(fm, Constants.TAG_PICK_GAME_FRAGMENT);
    }

    public static final void showInvitationDialog (final CallbackInterface onEnter,
            final CallbackInterface onCancel, InvitationDescriptor invitation) {
        android.app.FragmentManager fm =
                Multicards.getMainActivity().getFragmentManager();
        gameInvitationFragment = new InvitationFragment();
        gameInvitationFragment.setInvitationDetails(invitation);
        gameInvitationFragment.setOnClickOKListener(onEnter);
        gameInvitationFragment.setOnClickCancelListener(onCancel);
        gameInvitationFragment.show(fm, Constants.TAG_INVITATION_FRAGMENT);
        FragmentManager.setUIStates.add(Constants.UI_DIALOG_INCOMING_INVITATION);
    }

    public static final void showModalDialog(String strMessage, Activity activity) {
        if (activity == null)
            activity = Multicards.getMainActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(strMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static final void showProgressBar (String strMessage, final CallbackInterface onCancel) {
        hideProgressBar();
        progressDialog = ProgressDialog.show(Multicards.getMainActivity(), "", strMessage, true);
        progressDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (onCancel != null)
                            onCancel.onResponse(null);
                    }
                });
        progressDialog.setCancelable(true);
    }

    public static final void hideProgressBar () {
        if ((progressDialog != null) && (progressDialog.isShowing()))
            progressDialog.dismiss();
    }

    public static final void showUpdateDialog (Boolean boolMandatory, String strMessage,
                                               final ServerInfo serverInfo) {

        final String strURL = serverInfo.latest_apk;

        final String strLocalFilename = serverInfo.latest_ver + ".apk";
        AlertDialog.Builder alert = new AlertDialog.Builder(Multicards.getMainActivity());
        if (boolMandatory)
            alert.setMessage(Multicards.getMainActivity().getResources().
                    getString(R.string.alert_update_required) + " \n" + strMessage);
        else
            alert.setMessage(Multicards.getMainActivity().getResources().
                    getString(R.string.alert_new_version_available) + " \n" + strMessage);
        alert.setPositiveButton(R.string.string_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (strURL == null || strURL.isEmpty()) {
                            Utils.OpenPlayMarketPage();
                            return;
                        }
                        String type = Environment.DIRECTORY_DOWNLOADS;
                        File path = Environment.getExternalStoragePublicDirectory(type);
                        path.mkdirs();
                        final File file = new File(path, Constants.APP_FOLDER + "/" + strLocalFilename);
                        new FileUtils.DownloadTask(strURL, file.getAbsolutePath(),
                                new CallbackInterface() {
                                    public void onResponse(Object obj) {
                                        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                                .setDataAndType(Uri.fromFile(file),
                                                        "application/vnd.android.package-archive");
                                        Multicards.getMainActivity().startActivity(promptInstall);
                                    }
                                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        Gson gson = new Gson();
                        Multicards.getPreferences().strServerInfo = gson.toJson(serverInfo);
                        Multicards.getPreferences().savePreferences();
                    }
                });
        if (boolMandatory)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Multicards.getMainActivity().finish();
                    }
                });
            }
        if (!boolMandatory) {
            alert.setNegativeButton(R.string.string_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Gson gson = new Gson();
                            Multicards.getPreferences().strServerInfo = gson.toJson(serverInfo);
                            Multicards.getPreferences().savePreferences();
                        }
                    });
        }
        alert.show();
    }

    public static final void deliverError (ServerResponse response) {
        if (response.code == Constants.ERROR_USER_NOT_FOUND)
            showModalDialog(Multicards.getMainActivity().getResources().
                            getString(R.string.string_user_not_found), Multicards.getMainActivity());
    }

    public static final void showInvitationNotification(
            InvitationDescriptor invitation,
            PendingIntent intent, Context context) {
        String strContext = "";
        if ((invitation.user != null) && (invitation.user.name != null) &&
                (invitation.cardset != null) && (invitation.cardset.title != null))
            strContext = invitation.user.name + " " + context.getResources().
                    getString(R.string.string_invitation) + " " + invitation.cardset.title;
        Uri soundUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.
                Builder(context)
                .setSmallIcon(R.drawable.ic_logo_tiny5)
                .setContentTitle(context.getResources().
                        getString(R.string.string_game_invitation))
                .setSound(soundUri)
                .setLargeIcon(bitmap)
                .setVibrate(new long[] {0, 1000})
                .setContentText(strContext);

        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);

        NotificationManager notifyMgr = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Integer notificationID = 0;
        notifyMgr.notify(notificationID, mBuilder.build());
    }

    public static final void showUpdateNotification(
            ServerInfo serverInfo,
            PendingIntent intent, Context context) {
        String strMessage = "";
        if ((serverInfo.update_comment != null)
                && (Utils.getLocaleString(serverInfo.update_comment) != null))
            strMessage = Utils.getLocaleString(serverInfo.update_comment);
        Uri soundUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.
                Builder(context)
                .setSmallIcon(R.drawable.ic_logo_tiny5)
                .setContentTitle(context.getResources().
                        getString(R.string.alert_new_version_available))
                .setSound(soundUri)
                .setLargeIcon(bitmap)
                .setVibrate(new long[] {0, 1000})
                .setContentText(strMessage);

        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);

        NotificationManager notifyMgr = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Integer notificationID = 0;
        notifyMgr.notify(notificationID, mBuilder.build());
    }

    public static void clearNotification (Integer notificationID, Context context) {
        if (context == null)
            context = Multicards.getMainActivity();
        NotificationManager notifyMgr = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationID == null)
            notifyMgr.cancelAll();
        else
            notifyMgr.cancel(notificationID);
    }
}
