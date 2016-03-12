package memorizer.freecoders.com.flashcards.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.CardsetListAdapter;
import memorizer.freecoders.com.flashcards.classes.TagView;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.json.CardSet;
import memorizer.freecoders.com.flashcards.json.TagDescriptor;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletSearchResult;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 12.12.15.
 */
public class SearchCardsetFragment extends Fragment {
    private static String LOG_TAG = "SearchCardsetFragment";

    ListView cardSetListView;
    ListView popularCardSetListView;
    CardsetListAdapter cardSetListAdapter;
    CardsetListAdapter popularCardSetListAdapter;
    EditText inputEditText;
    TextView popularCardsetsTextView;
    Button buttonCardsetPicker;
    TextView textViewPoweredByQuizlet;
    LinearLayout linearLayoutTags;

    private ArrayList<String> pendingRequests;
    private ArrayList<String> selectedTags = new ArrayList<String>();
    private ArrayList<TagDescriptor> allTags = new ArrayList<TagDescriptor>();

    private int intFragmentType = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_cardset, container, false);

        popularCardsetsTextView = (TextView) view.findViewById(R.id.textViewPopularCardsets);
        popularCardSetListView = (ListView) view.findViewById(R.id.listViewPopularCardsets);
        popularCardSetListAdapter = new CardsetListAdapter(Multicards.getMainActivity());
        popularCardSetListView.setAdapter(popularCardSetListAdapter);

        cardSetListView = (ListView) view.findViewById(R.id.listViewCardSetPicker);
        cardSetListAdapter = new CardsetListAdapter(Multicards.getMainActivity());
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
                String strKeywords = "";
                try {
                    strKeywords = URLEncoder.encode(inputEditText.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.d(LOG_TAG, "UnsupportedEncodingException");
                }

                for (int i = 0; i < pendingRequests.size(); i++) {
                    ServerInterface.cancelRequestByTag(pendingRequests.get(i));
                    pendingRequests.remove(i);
                }
                String strTag = ServerInterface.searchCardsetQuizletRequest(
                        strKeywords,
                        new Response.Listener<QuizletSearchResult>() {
                            @Override
                            public void onResponse(QuizletSearchResult response) {
                                cardSetListAdapter.setQValues(response.sets);
                                cardSetListAdapter.notifyDataSetChanged();
                            }
                        }, null);
                pendingRequests.add(strTag);

                if (inputEditText.getText().toString().isEmpty()) {
                    popularCardSetListView.setVisibility(View.VISIBLE);
                    popularCardsetsTextView.setVisibility(View.VISIBLE);
                } else {
                    popularCardSetListView.setVisibility(View.GONE);
                    popularCardsetsTextView.setVisibility(View.GONE);
                }

                showTags(null, strKeywords);
            }
        });

        buttonCardsetPicker = (Button) view.findViewById(R.id.buttonCardSetPicker);

        cardSetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strGID = "";

                if (cardSetListAdapter.INT_ITEMS_TYPE == CardsetListAdapter.INT_ITEMS_TYPE_CARDSET) {
                    strGID = String.valueOf(cardSetListAdapter.values.get(position).gid);
                    Multicards.getPreferences().setRecentCardset
                            (cardSetListAdapter.values.get(position));
                } else {
                    strGID = "quizlet_" + String.valueOf(cardSetListAdapter.qvalues.get(position).id);
                    Multicards.getPreferences().setRecentCardset
                            (cardSetListAdapter.qvalues.get(position), strGID);
                }

                if (Multicards.onPickCardsetCallback != null)
                    Multicards.onPickCardsetCallback.onResponse(strGID);
            }
        });

        cardSetListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String strGID = "";
                if (cardSetListAdapter.INT_ITEMS_TYPE == CardsetListAdapter.INT_ITEMS_TYPE_CARDSET)
                    strGID = String.valueOf(cardSetListAdapter.values.get(position).gid);
                else
                    strGID = "quizlet_" + String.valueOf(cardSetListAdapter.qvalues.get(position).id);
                InputDialogInterface.showCardsetDetailsDialog(strGID);
                return true;
            }
        });

        popularCardSetListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String strGID = String.valueOf(
                        popularCardSetListAdapter.values.get(position).gid);
                Log.d(LOG_TAG, "Onlongclick " + strGID);
                InputDialogInterface.showCardsetDetailsDialog(strGID);
                return true;
            }
        });

        textViewPoweredByQuizlet = (TextView) view.findViewById(R.id.textViewPoweredByQuizlet);

        linearLayoutTags = (LinearLayout) view.findViewById(R.id.linearLayoutTags);

        pendingRequests = new ArrayList<String>();

        populateView();

        return view;
    }

    public void populateView() {
        ServerInterface.getPopularCardsetsRequest(new Response.Listener<ArrayList<CardSet>>() {
            @Override
            public void onResponse(ArrayList<CardSet> response) {
            if (response.size()>0) {
                Log.d(LOG_TAG, "Received " + response.size() + " items");
                popularCardsetsTextView.setVisibility(View.VISIBLE);
                popularCardSetListView.setVisibility(View.VISIBLE);
                popularCardSetListAdapter.setValues(response);
                popularCardSetListAdapter.notifyDataSetChanged();
                popularCardSetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final String strGID = String.valueOf(
                            popularCardSetListAdapter.values.get(position).gid);
                    if (Multicards.onPickCardsetCallback != null)
                        Multicards.onPickCardsetCallback.onResponse(strGID);
                    Multicards.getPreferences().
                            setRecentCardset(popularCardSetListAdapter.values.get(position));
                    }
                });
            }
            }
        }, null);

        textViewPoweredByQuizlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        Multicards.getMainActivity().getResources().
                                getString(R.string.string_powered_by_quizlet_link)));
                Multicards.getMainActivity().startActivity(browserIntent);
            }
        });

        initTags();
    }

    private void showTags (final ArrayList<TagDescriptor> tagsList, String strFilter) {
        ArrayList<TagDescriptor> tags = tagsList;
        if (tagsList == null)
            tags = allTags;
        else
            allTags = tagsList;
        Collections.sort(tags, new Comparator<TagDescriptor>() {
            @Override
            public int compare(TagDescriptor lhs, TagDescriptor rhs) {
                int rank_left = lhs.rank != null ? lhs.rank : 0;
                int rank_right = rhs.rank != null ? rhs.rank : 0;
                if ( rank_left == rank_right ) return lhs.getName().compareTo(rhs.getName());
                else return rank_left > rank_right ? -1 : 1;
            }
        });
        linearLayoutTags.removeAllViews();
        selectedTags.clear();
        for (int i = 0; i < tags.size(); i++) {
            final TagView tagView = new TagView(Multicards.getMainActivity());
            tagView.setTag(tags.get(i));
            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean boolSelected = tagView.onSelect();
                    if (boolSelected)
                        addTag(tagView.getTagID().toString());
                    else
                        removeTag(tagView.getTagID().toString());
                    Log.d(LOG_TAG, "Selected tags " + new Gson().toJson(selectedTags));
                    searchCardsetsByTags();
                }
            });
            if ((strFilter.isEmpty()) || (tags.get(i).getName().toLowerCase().
                    contains(strFilter.toLowerCase())) || tagView.boolSelected)
                linearLayoutTags.addView(tagView);
        }
    }

    private void initTags () {
        ArrayList<TagDescriptor> tags = new ArrayList<TagDescriptor>();
        Iterator it = Multicards.getPreferences().tagDescriptors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            TagDescriptor tag = (TagDescriptor) pair.getValue();
            tags.add(tag);
            it.remove(); // avoids a ConcurrentModificationException
        }
        showTags(tags, "");
        ServerInterface.getTagsRequest(new Response.Listener<ArrayList<TagDescriptor>>() {
            @Override
            public void onResponse(ArrayList<TagDescriptor> response) {
                showTags(response, "");
            }
        }, null);
    }

    private void searchCardsetsByTags () {
        if (selectedTags.size() > 0)
            ServerInterface.searchCardsetsRequest(selectedTags, new Response.Listener<ArrayList<CardSet>>() {
                @Override
                public void onResponse(ArrayList<CardSet> response) {
                    cardSetListAdapter.setValues(response);
                    cardSetListAdapter.notifyDataSetChanged();

                    if ((inputEditText.getText().toString().isEmpty()) && (selectedTags.size() == 0)) {
                        popularCardSetListView.setVisibility(View.VISIBLE);
                        popularCardsetsTextView.setVisibility(View.VISIBLE);
                    } else {
                        popularCardSetListView.setVisibility(View.GONE);
                        popularCardsetsTextView.setVisibility(View.GONE);
                    }
                }
            }, null);
        else {
            popularCardSetListView.setVisibility(View.VISIBLE);
            popularCardsetsTextView.setVisibility(View.VISIBLE);
            cardSetListAdapter.setQValues(new ArrayList<QuizletCardsetDescriptor>());
            cardSetListAdapter.notifyDataSetChanged();
        }
    }

    private void removeTag (String strTagID) {
        for (int i = 0; i < selectedTags.size(); i++) {
            if (selectedTags.get(i).equals(strTagID)) {
                selectedTags.remove(i);
                break;
            }
        }
    }

    private void addTag (String strTagID) {
        Boolean boolContains = false;
        for (int i = 0; i < selectedTags.size(); i++) {
            if (selectedTags.get(i).equals(strTagID)) {
                boolContains = true;
                break;
            }
        }
        if (!boolContains)
            selectedTags.add(strTagID);
    }
}
