package memorizer.freecoders.com.flashcards;

import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.android.volley.Response;

import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.Question;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.network.SocketInterface;

/**
 * Created by alex-mac on 26.12.15.
 */
public class GameplayManager {

    public static ProgressDialog progressDialog;

    public static final void startSingleplayerGame() {
    }

    public static final void playAgain() {
    }

    public static final void quitSingleplayerGame() {
    }

    public static final void newServerQuestion(Question question) {
        if (Multicards.getMainActivity().intUIState != Constants.UI_STATE_MULTIPLAYER_MODE) {
            hideMainMenu();
            hideCardsetPickerActivity();
            Multicards.getMainActivity().intUIState = Constants.UI_STATE_MULTIPLAYER_MODE;
        }
        if ((progressDialog != null) && (progressDialog.isShowing()))
            progressDialog.dismiss();

        Multicards.getMultiplayerInterface().eventNewQuestion(question);
    }

    public static final void nextFlashcard () {

    }

    public static final void requestMultiplayerGame (final String strGID) {
        if ((Multicards.getPreferences().strUserID == null) ||
                Multicards.getPreferences().strUserID.isEmpty())
            return;

        if ((Multicards.getPreferences().strUserName == null) ||
                Multicards.getPreferences().strUserName.isEmpty())
            InputDialogInterface.updateUserName(new CallbackInterface() {
                @Override
                public void onResponse(Object obj) {
                    ServerInterface.newGameRequest(strGID,
                        new Response.Listener<Game>() {
                            @Override
                            public void onResponse(Game response) {
                                if (response.status == Constants.GAME_STATUS_SEARCHING_PLAYERS) {
                                    Multicards.getMultiplayerInterface().setGameData(null, strGID);
                                    String strMessage = Multicards.getMainActivity().
                                            getResources().getString(
                                            R.string.waiting_opponent_dialog_message);
                                    progressDialog = ProgressDialog.show(
                                            Multicards.getMainActivity(), "", strMessage, true);
                                    progressDialog.setOnCancelListener(
                                            new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            Multicards.getMultiplayerInterface().quitGame();
                                        }
                                    });
                                }
                                progressDialog.setCancelable(true);
                            }
                        }, null);
                }
            });
        else
            ServerInterface.newGameRequest(strGID,
                    new Response.Listener<Game>() {
                        @Override
                        public void onResponse(Game response) {
                            if (response.status == Constants.GAME_STATUS_SEARCHING_PLAYERS) {
                                Multicards.getMultiplayerInterface().setGameData(null, strGID);
                                String strMessage = Multicards.getMainActivity().
                                        getResources().getString(
                                        R.string.waiting_opponent_dialog_message);
                                progressDialog = ProgressDialog.show(
                                        Multicards.getMainActivity(),
                                        "",
                                        strMessage, true);
                                progressDialog.setCancelable(true);
                                progressDialog.setOnCancelListener(
                                        new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                Multicards.getMultiplayerInterface().quitGame();
                                            }
                                        });
                            }
                        }
                    }, null);
    }

    public static final void startMultiplayerGame(Game game) {
        Multicards.getMultiplayerInterface().setGameData(game, null);
        Multicards.getMainActivity().showPlayersInfo();
        Multicards.getMainActivity().playersInfoFragment.updateGameInfo(game);

    }

    public static final void quitMultilayerGame() {
        Multicards.getMainActivity().returnToMainMenu();
        InputDialogInterface.showGameOverMessage(null);
        Multicards.getMainActivity().intUIState = Constants.UI_STATE_MAIN_MENU;
    }

    private static final void hideMainMenu () {
        if (Multicards.getMainActivity().mainMenuFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction().
                    remove(Multicards.getMainActivity().mainMenuFragment).commit();
    }

    private static final void showMainMenu () {
    }

    private static final void hideCardsetPickerActivity () {
        if (Multicards.getCardsetPickerActivity() != null)
            Multicards.getCardsetPickerActivity().finish();
    }

}
