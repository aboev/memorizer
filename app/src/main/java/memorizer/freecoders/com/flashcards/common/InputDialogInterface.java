package memorizer.freecoders.com.flashcards.common;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 05.12.15.
 */
public class InputDialogInterface {

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

    public static final void showGameOverMessage (final CallbackInterface onReply) {

        String strMessage = "Game over. ";

        if (FragmentManager.playersInfoFragment.player1Score >
                FragmentManager.playersInfoFragment.player2Score )
            strMessage = strMessage + "You win!";
        else
            strMessage = strMessage +
                    FragmentManager.playersInfoFragment.player2Name + " win";

        Context context = Multicards.getMainActivity();

        new AlertDialog.Builder(context)
                .setTitle("Game")
                .setMessage(strMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .setNegativeButton("Play again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (Multicards.getMultiplayerInterface().currentGame != null) {
                            GameplayManager.requestMultiplayerGame(
                                    Multicards.getMultiplayerInterface().currentGame.strGID);
                            Multicards.getMainActivity().intUIState =
                                    Constants.UI_STATE_MULTIPLAYER_MODE;
                        }
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
                .setNegativeButton("Play again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (Multicards.getMultiplayerInterface().currentGame != null) {
                            GameplayManager.requestMultiplayerGame(
                                    Multicards.getMultiplayerInterface().currentGame.strGID);
                            Multicards.getMainActivity().intUIState =
                                    Constants.UI_STATE_MULTIPLAYER_MODE;
                        }
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

    public static final void showEnterOpponentNameDialog (final CallbackInterface onEnter) {
        Context context = Multicards.getMainActivity();
        final EditText txtNickname = new EditText(context);

        String strTitle = Multicards.getMainActivity().
                getResources().getString(R.string.string_enter_opponent_nickname);

        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(strTitle)
                .setView(txtNickname)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String strName = txtNickname.getText().toString();
                        onEnter.onResponse(strName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onEnter.onResponse(null);
                    }
                })
                .show();
    }
}
