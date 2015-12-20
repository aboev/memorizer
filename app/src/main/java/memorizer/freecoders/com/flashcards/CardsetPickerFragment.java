package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.classes.AutoResizeTextView;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.CardsetListAdapter;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.classes.ListViewAdapter;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.MemorizerApplication;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletSearchResult;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 12.12.15.
 */
public class CardsetPickerFragment extends Fragment {
    private static String LOG_TAG = "CardsetPickerFragment";

    ListView cardSetListView;
    CardsetListAdapter cardSetListAdapter;
    EditText inputEditText;
    Button buttonCardsetPicker;
    int intNextFragment;

    public ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.cardsetpicker, container, false);

        cardSetListView = (ListView) view.findViewById(R.id.listViewCardSetPicker);
        cardSetListAdapter = new CardsetListAdapter(MemorizerApplication.getMainActivity());
        cardSetListView.setAdapter(cardSetListAdapter);

        inputEditText = (EditText) view.findViewById(R.id.editTextCardSetPicker);
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ServerInterface.searchCardsetQuizletRequest(inputEditText.getText().toString(),
                        new Response.Listener<QuizletSearchResult>() {
                            @Override
                            public void onResponse(QuizletSearchResult response) {
                                cardSetListAdapter.setValues(response.sets);
                                cardSetListAdapter.notifyDataSetChanged();
                            }
                        }, null);
            }
        });

        buttonCardsetPicker = (Button) view.findViewById(R.id.buttonCardSetPicker);
        buttonCardsetPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemorizerApplication.getMainActivity().nextFlashCard();

                MemorizerApplication.getMainActivity().scoreView =
                        (TextView) MemorizerApplication.getMainActivity().
                                findViewById(R.id.scoreView);

                MemorizerApplication.getMainActivity().intUIState = Constants.UI_STATE_TRAIN_MODE;

                MemorizerApplication.getMainActivity().showPlayersInfo();
            }
        });

        cardSetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strSetID = String.valueOf(cardSetListAdapter.values.get(position).id);
                String strGID = "quizlet_" + strSetID;
                Cardset cardset = MemorizerApplication.getFlashCardsDAO().fetchCardset(strGID);
                if ((cardset == null) && (intNextFragment == Constants.UI_STATE_TRAIN_MODE)) {
                    MemorizerApplication.getFlashCardsDAO().importFromWeb(strGID,
                            new CallbackInterface() {
                                @Override
                                public void onResponse(Object obj) {
                                    if (progressDialog != null)
                                        progressDialog.dismiss();
                                    Long setID = (Long) obj;
                                    MemorizerApplication.getMainActivity().setSetID(setID);
                                    MemorizerApplication.getMainActivity().nextFlashCard();
                                    MemorizerApplication.getMainActivity().scoreView =
                                            (TextView) MemorizerApplication.getMainActivity().
                                                    findViewById(R.id.scoreView);
                                    MemorizerApplication.getMainActivity().intUIState =
                                            Constants.UI_STATE_TRAIN_MODE;
                                    MemorizerApplication.getMainActivity().showPlayersInfo();
                                    getFragmentManager().beginTransaction().remove(
                                            MemorizerApplication.getMainActivity().
                                                    cardsetPickerFragment).commit();
                                }
                            }, new CallbackInterface() {
                                @Override
                                public void onResponse(Object obj) {
                                    if (progressDialog != null)
                                        progressDialog.dismiss();
                                    Toast.makeText(MemorizerApplication.getMainActivity(),
                                            "Failed to fetch cardset",
                                            Toast.LENGTH_LONG).show();
                                    MemorizerApplication.getMainActivity().returnToMainMenu();
                                }
                            });
                    progressDialog = ProgressDialog.show(
                            MemorizerApplication.getMainActivity(), "", "Downloading cardset", true);
                    progressDialog.setCancelable(true);
                } else if ((cardset != null) && (intNextFragment == Constants.UI_STATE_TRAIN_MODE)) {
                    Long setID = cardset.getId();
                    MemorizerApplication.getMainActivity().setSetID(setID);
                    MemorizerApplication.getMainActivity().nextFlashCard();
                    MemorizerApplication.getMainActivity().scoreView =
                            (TextView) MemorizerApplication.getMainActivity().
                                    findViewById(R.id.scoreView);
                    MemorizerApplication.getMainActivity().intUIState =
                            Constants.UI_STATE_TRAIN_MODE;
                    MemorizerApplication.getMainActivity().showPlayersInfo();
                    getFragmentManager().beginTransaction().remove(
                            MemorizerApplication.getMainActivity().
                                    cardsetPickerFragment).commit();
                } else if (intNextFragment == Constants.UI_STATE_MULTIPLAYER_MODE) {
                    MultiplayerInterface multiplayerInterface = new MultiplayerInterface();
                    MemorizerApplication.setMultiPlayerInterface(multiplayerInterface);
                    multiplayerInterface.requestNewGame(strGID);
                    getFragmentManager().beginTransaction().remove(
                            MemorizerApplication.getMainActivity().
                                    cardsetPickerFragment).commit();
                }
            }
        });

        return view;
    }

    public void setNextFragment (int intNextFragment) {
        this.intNextFragment = intNextFragment;
    }
}
