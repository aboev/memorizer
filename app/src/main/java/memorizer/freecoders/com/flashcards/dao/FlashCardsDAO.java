package memorizer.freecoders.com.flashcards.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.activeandroid.query.Select;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.quizlet.QuizletCardsetDescriptor;
import memorizer.freecoders.com.flashcards.network.ServerInterface;

/**
 * Created by alex-mac on 01.11.15.
 */
public class FlashCardsDAO {
    Context context;
    private String LOG_TAG = "FlashCardsDAO";
    private Random ran = new Random();
    private int intCardsCount = 4;

    public FlashCardsDAO(Context context) {
        this.context = context;
    }

    public void loadFromCSV(String strFilename){
        Log.d("FlashCardsDAO", "Loading items from csv");
        AssetManager assetManager = context.getAssets();

        try {
            InputStream csvStream = assetManager.open(strFilename);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream, "UTF-16");
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;
            int cnt = 0;

            // throw away the header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                if (line.length > 1) {
                    Card card = new Card();
                    card.question = line[0];
                    card.answer = line[1];
                    card.save();
                    cnt++;
                }
            }

            Log.d(LOG_TAG, "Loaded " + cnt + " cards");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importFromWeb(final String gid, final CallbackInterface onSuccess,
                              final CallbackInterface onFail) {
        Log.d(LOG_TAG, "Import cardset from web for " + gid);
        if (gid.split("_")[0].equals("quizlet")) {
            String setid = gid.split("_")[1];
            ServerInterface.fetchQuizletCardsetRequest(setid,
                new Response.Listener<QuizletCardsetDescriptor>() {
                    @Override
                    public void onResponse(QuizletCardsetDescriptor response) {
                        Cardset cardset = new Cardset();
                        cardset.gid = "quizlet_" + response.id;
                        cardset.title = response.title;
                        cardset.created_by = response.created_by;
                        cardset.url = response.url;
                        cardset.save();
                        int cnt = 0;
                        for (int i = 0; i < response.terms.size(); i++) {
                            Card card = new Card();
                            card.question = response.terms.get(i).term;
                            card.answer = response.terms.get(i).definition;
                            card.setID = cardset.getId();
                            card.save();
                            cnt++;
                        }
                        if (cnt > 0) {
                            onSuccess.onResponse(cardset.getId());
                        } else
                            onFail.onResponse(null);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onFail.onResponse(null);
                    }
                }
            );
        } else {
            onFail.onResponse(null);
        }
    }

    public FlashCard fetchRandomCard(){
        List<Card> cards = new Select()
                .from(Card.class)
                .orderBy("RANDOM()")
                .limit(intCardsCount + 1)
                .execute();
        if (cards == null) return null;

        FlashCard flashCard = new FlashCard();

        for (int i=1; i < cards.size(); i++)
            flashCard.options.add(cards.get(i).answer);

        int intAnswerPos = ran.nextInt(intCardsCount);

        flashCard.question = cards.get(0).question;
        flashCard.options.set(intAnswerPos, cards.get(0).answer);
        flashCard.answer_id = intAnswerPos;
        return flashCard;
    }

    public FlashCard fetchRandomCard(Long setID){
        List<Card> cards = new Select()
                .from(Card.class)
                .where("SetID = ?", setID)
                .orderBy("RANDOM()")
                .limit(intCardsCount + 1)
                .execute();
        if (cards == null) return null;

        FlashCard flashCard = new FlashCard();

        for (int i=1; i < cards.size(); i++)
            flashCard.options.add(cards.get(i).answer);

        int intAnswerPos = ran.nextInt(intCardsCount);

        flashCard.question = cards.get(0).question;
        flashCard.options.set(intAnswerPos, cards.get(0).answer);
        flashCard.answer_id = intAnswerPos;
        return flashCard;
    }

    public Cardset fetchCardset(String strGID){
        List<Cardset> cardsets = new Select()
                .from(Cardset.class)
                .where("gid = ?", strGID)
                .limit(1)
                .execute();
        if (cardsets.size() > 0) {
            Multicards.getPreferences().recentSets.put(strGID, System.currentTimeMillis());
            Multicards.getPreferences().savePreferences();
            Log.d(LOG_TAG, "Saving recent sets " +
                    new Gson().toJson(Multicards.getPreferences().recentSets) );
            return cardsets.get(0);
        } else return null;
    }

    public void setRecentCardset (String strGID) {
        Log.d(LOG_TAG, "Setting recent cardset " +  strGID);
        Multicards.getPreferences().recentSets.put(strGID, System.currentTimeMillis());
        Multicards.getPreferences().savePreferences();
    }
}
