package memorizer.freecoders.com.flashcards.dao;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.activeandroid.query.Select;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.classes.ListViewAdapter;

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
        Card card = new Select()
                .from(Card.class)
                .orderBy("RANDOM()")
                .executeSingle();
        if (card == null)
            loadFromCSV("english.csv");
        else
            Log.d("FlashCardsDAO", "Read item " + card.question);

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
}
