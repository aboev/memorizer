package memorizer.freecoders.com.flashcards.common;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 05.12.15.
 */
public class InputDialogInterface {

    public static final void askUserName (final CallbackInterface onReply) {

        Context context = MemorizerApplication.getMainActivity();
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
                                    MemorizerApplication.getPreferences().strUserName = userDetails.name;
                                    MemorizerApplication.getPreferences().savePreferences();
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

        Context context = MemorizerApplication.getMainActivity();
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
}
