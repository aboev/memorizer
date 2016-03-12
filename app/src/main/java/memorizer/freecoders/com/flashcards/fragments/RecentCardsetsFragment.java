package memorizer.freecoders.com.flashcards.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.internal.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.CardsetListAdapter;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 26.12.15.
 */
public class RecentCardsetsFragment extends Fragment{
    private static String LOG_TAG = "RecentCardsetsFragment";

    ListView cardSetListView;
    CardsetListAdapter cardSetListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent_cardsets, container, false);

        cardSetListView = (ListView) view.findViewById(R.id.listViewCardSetPicker);
        cardSetListAdapter = new CardsetListAdapter(Multicards.getMainActivity());
        cardSetListView.setAdapter(cardSetListAdapter);

        cardSetListAdapter.setQValues(fetchRecentFlashcards());
        cardSetListAdapter.notifyDataSetChanged();

        cardSetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strGID = "";
                if (cardSetListAdapter.INT_ITEMS_TYPE == CardsetListAdapter.INT_ITEMS_TYPE_CARDSET)
                    strGID = String.valueOf(cardSetListAdapter.values.get(position).gid);
                else
                    strGID = String.valueOf(cardSetListAdapter.qvalues.get(position).gid);

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
                    strGID = String.valueOf(cardSetListAdapter.qvalues.get(position).gid);

                InputDialogInterface.showCardsetDetailsDialog(strGID);
                return true;
            }
        });

        return view;
    }

    public ArrayList<QuizletCardsetDescriptor> fetchRecentFlashcards () {
        ArrayList<QuizletCardsetDescriptor> cardsetDescriptors =
                new ArrayList<QuizletCardsetDescriptor>();

        Gson gson = new Gson();
        Map<Integer, Long> map = sortByValues(Multicards.getPreferences().recentSets);
        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String strGID = (String) entry.getKey();
            Cardset cardset = Multicards.getFlashCardsDAO().fetchCardset(strGID, false);
            if (cardset != null) {
                QuizletCardsetDescriptor descriptor = new QuizletCardsetDescriptor();
                descriptor.title = cardset.title;
                descriptor.created_by = cardset.created_by;
                if (Utils.getSetID(cardset.gid) != null) descriptor.id = Utils.getSetID(cardset.gid);
                descriptor.gid = cardset.gid;
                descriptor.lang_terms = cardset.lang_terms;
                descriptor.lang_definitions = cardset.lang_definitions;
                cardsetDescriptors.add(descriptor);
            } else {
                if (Multicards.getPreferences().recentSetDescriptors.containsKey(strGID)) {
                    QuizletCardsetDescriptor descriptor =
                            Multicards.getPreferences().recentSetDescriptors.get(strGID);
                    if (descriptor.title == null) descriptor.title = "";
                    cardsetDescriptors.add(descriptor);
                }
            }

        }
        return cardsetDescriptors;
    }

    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
}
