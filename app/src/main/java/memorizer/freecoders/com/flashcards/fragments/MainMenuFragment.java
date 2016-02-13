package memorizer.freecoders.com.flashcards.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import memorizer.freecoders.com.flashcards.json.CardSet;
import memorizer.freecoders.com.flashcards.json.Game;
import memorizer.freecoders.com.flashcards.json.ServerResponse;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 22.11.15.
 */
public class MainMenuFragment extends Fragment {
    private TextView textViewTrain;
    private TextView textViewMultiplayer;
    private TextView textViewSettings;
    private ImageView imageViewInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        textViewTrain = (TextView) view.findViewById(R.id.TextView_StyleButtonTrain);
        textViewMultiplayer = (TextView) view.findViewById(R.id.TextView_StyleButtonMultiplayer);
        textViewSettings = (TextView) view.findViewById(R.id.TextView_StyleButtonSettings);
        imageViewInfo = (ImageView) view.findViewById(R.id.imageViewInfo);

        textViewTrain.setOnClickListener(new View.OnClickListener() {
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

        textViewMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            InputDialogInterface.showMultiplayerDialog(new CallbackInterface() {
                @Override
                public void onResponse(Object obj) {
                Integer index = (Integer) obj;
                if (index == 0) {
                    Multicards.onPickCardsetCallback = new CallbackInterface() {
                        @Override
                        public void onResponse(Object obj) {
                            String strGID = (String) obj;
                            requestMultiplayerGame(true, strGID);
                        }
                    };

                    Intent intent = new Intent(Multicards.getMainActivity(),
                            CardsetPickerActivity.class);
                    startActivity(intent);
                } else if (index == 1) {
                    InputDialogInterface.showEnterOpponentNameDialog(new CallbackInterface() {
                        @Override
                        public void onResponse(Object obj) {
                            if (obj != null) {
                                String strOpponentName = (String) obj;
                                GameplayManager.strOpponentName = strOpponentName;
                                ServerInterface.newGameRequest("", strOpponentName,
                                    new Response.Listener<ServerResponse<Game>>() {
                                        @Override
                                        public void onResponse(ServerResponse<Game> response) {
                                            if (!response.isSuccess())
                                                InputDialogInterface.deliverError(response);
                                        }
                                    }, null);
                            }
                        }
                    });
                }
                }
            });
            }
        });

        textViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager.showUserProfileFragment(false);
            }
        });

        imageViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialogInterface.showModalDialog(Multicards.getMainActivity().
                        getResources().getString(R.string.aboutText),
                        Multicards.getMainActivity());
            }
        });

        if (Multicards.getMainActivity() != null)
            FragmentManager.intUIState = Constants.UI_STATE_MAIN_MENU;

        return view;
    }

    private void saveRecentOpponent (String strOpponentName) {

    }

    private void requestMultiplayerGame(final Boolean boolNewGame, final String strGID) {
        if (boolNewGame) {
            InputDialogInterface.showEnterOpponentNameDialog(new CallbackInterface() {
                @Override
                public void onResponse(Object obj) {
                    String strOpponentName = (obj != null) ? (String) obj : null;
                    GameplayManager.strOpponentName = strOpponentName;
                    GameplayManager.requestMultiplayerGameNew(boolNewGame, strOpponentName, strGID);
                }
            });
            Multicards.getCardsetPickerActivity().finish();
        } else {
            GameplayManager.requestMultiplayerGame(strGID);
            Multicards.getCardsetPickerActivity().finish();
        }
    }

    private void startSinglePlayer (final String strGID) {
        Cardset cardset = Multicards.getFlashCardsDAO().fetchCardset(strGID);
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
