package memorizer.freecoders.com.flashcards.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;

import memorizer.freecoders.com.flashcards.CardsetPickerActivity;
import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.ServerResponse;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.network.SocketInterface;

/**
 * Created by alex-mac on 22.11.15.
 */
public class MainMenuFragment extends Fragment {

    private FrameLayout btnTrain;
    private FrameLayout btnMultiplayer;
    private FrameLayout btnSettings;

    private ImageView imageViewInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        btnTrain = (FrameLayout) view.findViewById(R.id.btnTrain);
        btnMultiplayer = (FrameLayout) view.findViewById(R.id.btnMultiplayer);
        btnSettings = (FrameLayout) view.findViewById(R.id.btnSettings);

        imageViewInfo = (ImageView) view.findViewById(R.id.imageViewInfo);

        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Multicards.onPickCardsetCallback = new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        String strGID = (String) obj;
                        startSinglePlayer(strGID);
                    }
                };
                Intent intent = new Intent(Multicards.getMainActivity(),
                        CardsetPickerActivity.class);
                startActivity(intent);
            }
        });

        btnMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialogInterface.showMultiplayerDialog(new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        Integer index = (Integer) obj;
                        if (index == 0) {   // Start new game
                            Multicards.onPickCardsetCallback = new CallbackInterface() {
                                @Override
                                public void onResponse(Object obj) {
                                    String strGID = (String) obj;
                                    requestMultiplayerGameStart(strGID);
                                }
                            };

                            Intent intent = new Intent(Multicards.getMainActivity(),
                                    CardsetPickerActivity.class);
                            startActivity(intent);
                        } else if (index == 1) {    // Join existing game
                            requestMultiplayerGameJoin();
                        }
                    }
                });
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager.showUserProfileFragment(false);
            }
        });

        imageViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strVersion = "";
                try {
                    PackageInfo pInfo = Multicards.getMainActivity().
                            getPackageManager().getPackageInfo(
                            Multicards.getMainActivity().getPackageName(), 0);
                    strVersion = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String strMessage = Multicards.getMainActivity().
                        getResources().getString(R.string.aboutText) + "\n" +
                        strVersion;
                InputDialogInterface.showModalDialog(strMessage, Multicards.getMainActivity());
            }
        });

        if (Multicards.getMainActivity() != null)
            FragmentManager.intUIState = Constants.UI_STATE_MAIN_MENU;

        return view;
    }

    private void saveRecentOpponent (String strOpponentName) {

    }

    private void requestMultiplayerGameStart(final String strGID) {
        InputDialogInterface.showEnterOpponentNameDialog(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                String strOpponentName = (obj != null) ? (String) obj : null;
                GameplayManager.strOpponentName = strOpponentName;
                GameplayManager.requestMultiplayerGame(strOpponentName, strGID);
            }
        });
        Multicards.getCardsetPickerActivity().finish();
    }

    private void requestMultiplayerGameJoin(){
        InputDialogInterface.showChooseGameDialog(new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                if (obj != null) {
                    String strOpponentName = (String) obj;
                    GameplayManager.strOpponentName = strOpponentName;
                    String strMessage = getResources().
                            getString(R.string.waiting_opponent_dialog_message);
                    InputDialogInterface.showProgressBar(strMessage, null);
                    ServerInterface.startGameRequest(false, "", strOpponentName,
                            new Response.Listener<ServerResponse<Game>>() {
                                @Override
                                public void onResponse(ServerResponse<Game> response) {
                                    if (!response.isSuccess())
                                        InputDialogInterface.deliverError(response);
                                    SocketInterface.emitStatusUpdate(
                                            Constants.PLAYER_STATUS_WAITING);
                                    if (InputDialogInterface.progressDialog != null)
                                        InputDialogInterface.progressDialog.dismiss();
                                }
                            }, null);
                }
            }
        });
    }

    private void startSinglePlayer (final String strGID) {
        Cardset cardset = Multicards.getFlashCardsDAO().fetchCardset(strGID, true);
        if (cardset == null) {
            Multicards.getFlashCardsDAO().importFromWeb(strGID,
                new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        if (InputDialogInterface.progressDialog != null)
                            InputDialogInterface.progressDialog.dismiss();
                        Long setID = (Long) obj;
                        GameplayManager.startSingleplayerGame(setID, strGID);
                        Multicards.getFlashCardsDAO().setRecentCardset(strGID);
                        Multicards.getCardsetPickerActivity().finish();
                    }
                }, new CallbackInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        Multicards.getMainActivity().getFragmentManager().beginTransaction().
                                detach(FragmentManager.mainMenuFragment).commit();
                        if (InputDialogInterface.progressDialog != null)
                            InputDialogInterface.progressDialog.dismiss();
                        Toast.makeText(Multicards.getMainActivity(),
                                "Failed to fetch cardset",
                                Toast.LENGTH_LONG).show();
                        FragmentManager.returnToMainMenu(false);
                    }
                });
            String strMessage = getResources().
                    getString(R.string.download_cardset_dialog_message);
            InputDialogInterface.showProgressBar(strMessage, null);
        } else {
            GameplayManager.startSingleplayerGame(cardset.getId(), strGID);
            Multicards.getFlashCardsDAO().setRecentCardset(strGID);
            Multicards.getCardsetPickerActivity().finish();
        }
    }

}
