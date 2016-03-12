package memorizer.freecoders.com.flashcards.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.classes.ListViewAdapter;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Card;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.json.Image;
import memorizer.freecoders.com.flashcards.json.InvitationDescriptor;
import memorizer.freecoders.com.flashcards.json.QCardset;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 23.01.16.
 */
public class CardsetDetailsFragment extends DialogFragment {

    private static String LOG_TAG = "CardsetDetailsFragment";

    TextView textViewAuthor;
    TextView textViewCardsetName;

    ImageView imageViewLangFrom;
    ImageView imageViewLangTo;

    ListView listViewCardset;

    LinearLayout linearLayoutLoading;

    ArrayAdapter<String> arrayAdapter;

    private String strGID;

    public CardsetDetailsFragment() {

    }

    public void setGID(String strGID) {
        this.strGID = strGID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcard_details, container);

        textViewCardsetName = (TextView) view.findViewById(R.id.textViewCardsetName);
        textViewAuthor = (TextView) view.findViewById(R.id.textViewAuthor);

        imageViewLangFrom = (ImageView) view.findViewById(R.id.imageViewLangFrom);
        imageViewLangTo = (ImageView) view.findViewById(R.id.imageViewLangTo);

        listViewCardset = (ListView) view.findViewById(R.id.listViewCardset);

        linearLayoutLoading = (LinearLayout) view.findViewById(R.id.linearLayoutLoading);

        arrayAdapter = new ArrayAdapter<String>(Multicards.getMainActivity(),
                android.R.layout.simple_list_item_1, new ArrayList<String>());
        listViewCardset.setAdapter(arrayAdapter);

        populateView();

        return view;
    }

    private void populateView () {
        showProgress(true);

        String strLangFrom = "";
        String strLangTo = "";
        String strAuthor = "";
        String strCardsetTitle = "";

        Cardset cardset = Multicards.getFlashCardsDAO().fetchCardset(strGID, false);
        if (cardset != null) {
            strCardsetTitle = cardset.title;
            strAuthor = cardset.created_by;
            if (!cardset.inverted){
                strLangFrom = cardset.lang_terms;
                strLangTo = cardset.lang_definitions;
            } else {
                strLangFrom = cardset.lang_definitions;
                strLangTo = cardset.lang_terms;
            }

            if (Utils.getCountryFlagByLang(strLangFrom) != null)
                imageViewLangFrom.setImageDrawable(Utils.getCountryFlagByLang(strLangFrom));

            if (Utils.getCountryFlagByLang(strLangTo) != null)
                imageViewLangTo.setImageDrawable(Utils.getCountryFlagByLang(strLangTo));

            textViewAuthor.setText(strAuthor);
            textViewCardsetName.setText(strCardsetTitle);

            List<Card> cards = Multicards.getFlashCardsDAO().fetchCards(cardset.getId());
            ArrayList<String> items = new ArrayList<String>();
            arrayAdapter.clear();
            for (int i = 0; i < cards.size(); i++) {
                String item = cards.get(i).question + ": " + cards.get(i).answer;
                arrayAdapter.add(item);
            }
            arrayAdapter.notifyDataSetChanged();
            showProgress(false);
        } else {
            String strSetID = Utils.parseGID(strGID)[1];
            ServerInterface.fetchQuizletCardsetRequest(strSetID,
                new Response.Listener<QuizletCardsetDescriptor>() {
                    @Override
                    public void onResponse(QuizletCardsetDescriptor response) {
                        textViewCardsetName.setText(response.title);
                        textViewAuthor.setText(response.created_by);
                        String strLangFrom = response.lang_terms;
                        String strLangTo = response.lang_definitions;
                        if (Utils.getCountryFlagByLang(strLangFrom) != null)
                            imageViewLangFrom.setImageDrawable(
                                    Utils.getCountryFlagByLang(strLangFrom));

                        if (Utils.getCountryFlagByLang(strLangTo) != null)
                            imageViewLangTo.setImageDrawable(
                                    Utils.getCountryFlagByLang(strLangTo));

                        for (int i = 0; i < response.terms.size(); i++) {
                            String strItem = response.terms.get(i).term + ": " +
                                    response.terms.get(i).definition;
                            arrayAdapter.add(strItem);
                        }
                        arrayAdapter.notifyDataSetChanged();
                        showProgress(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textViewAuthor.setText("");
                        textViewCardsetName.setText("");
                        showProgress(false);
                    }
                });
        }
    }

    private void showProgress(Boolean boolShow) {
        if (boolShow) {
            linearLayoutLoading.setVisibility(View.VISIBLE);
            imageViewLangFrom.setVisibility(View.GONE);
            imageViewLangTo.setVisibility(View.GONE);
            listViewCardset.setVisibility(View.GONE);
            textViewAuthor.setVisibility(View.GONE);
            textViewCardsetName.setVisibility(View.GONE);
        } else {
            linearLayoutLoading.setVisibility(View.GONE);
            imageViewLangFrom.setVisibility(View.VISIBLE);
            imageViewLangTo.setVisibility(View.VISIBLE);
            listViewCardset.setVisibility(View.VISIBLE);
            textViewAuthor.setVisibility(View.VISIBLE);
            textViewCardsetName.setVisibility(View.VISIBLE);
        }
    }

    public void dismissFragment () {
        Fragment prev = Multicards.getMainActivity().
                getFragmentManager().findFragmentByTag(Constants.TAG_DETAILS_FRAGMENT);
        FragmentManager.setUIStates.remove(Constants.UI_DIALOG_CARDSET_DETAILS);
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

}
