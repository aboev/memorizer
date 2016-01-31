package memorizer.freecoders.com.flashcards.classes;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Card;
import memorizer.freecoders.com.flashcards.dao.Cardset;

/**
 * Created by alex-mac on 24.01.16.
 */
public class GameplayData {
    private static String LOG_TAG = "GameplayData";

    public String strGID;
    public Cardset cardset;
    public ArrayList<FlashCard> questions;
    public ArrayList<Integer> answers;
    public ArrayList<Boolean> checks;
    public int intCurrentQuestion = 0;

    private Random ran = new Random();

    public GameplayData (String strGID) {
        Cardset cardset = Multicards.getFlashCardsDAO().fetchCardset(strGID);
        this.strGID = strGID;
        ArrayList<Card> cards;
        ArrayList<String> terms = new ArrayList<String>();
        ArrayList<String> definitions = new ArrayList<String>();
        answers = new ArrayList<Integer>();
        checks = new ArrayList<Boolean>();

        cards = Multicards.getFlashCardsDAO().fetchRandomCards(cardset.getId(),
                Constants.GAMEPLAY_QUESTIONS_PER_GAME);
        if ((cards.size() <= Constants.GAMEPLAY_OPTIONS_PER_QUESTION) || cardset == null)
            return;

        for (int i = 0; i < cards.size(); i++) {
            if (!cardset.inverted) {
                terms.add(cards.get(i).question);
                definitions.add(cards.get(i).answer);
            } else {
                definitions.add(cards.get(i).question);
                terms.add(cards.get(i).answer);
            }
        }

        questions = makeQuestions(terms, definitions);

        intCurrentQuestion = 0;
    }

    public ArrayList<FlashCard> makeQuestions (ArrayList<String> terms,
            ArrayList<String> definitions) {
        ArrayList<FlashCard> res = new ArrayList<FlashCard>();
        ArrayList<Integer> termIdList = getRandomNumbers(terms.size(), terms.size(), -1);
        int i = 0;
        while ((i < terms.size()) && (i < Constants.GAMEPLAY_QUESTIONS_PER_GAME)) {
            int termID = termIdList.get(i);
            int answerID = ran.nextInt(Constants.GAMEPLAY_OPTIONS_PER_QUESTION);

            FlashCard question = new FlashCard();
            ArrayList<Integer> optionIdList = getRandomNumbers(
                    Constants.GAMEPLAY_OPTIONS_PER_QUESTION - 1, definitions.size(), termID);

            for (int j = 0; j < optionIdList.size(); j++) {
                if (j == answerID) question.options.add(definitions.get(termID));
                question.options.add(definitions.get(optionIdList.get(j)));
            }
            if (answerID == optionIdList.size())
                question.options.add(definitions.get(termID));

            question.question = terms.get(termID);
            question.answer_id = answerID;
            res.add(question);

            answers.add(-1);
            checks.add(false);
            i++;
        }
        return res;
    }

    public FlashCard getNextQuestion () {
        if (intCurrentQuestion >= questions.size())
            return null;
        FlashCard res = questions.get(intCurrentQuestion);
        intCurrentQuestion++;
        return res;
    }

    public void setAnswer (int answer_id) {
        answers.set(intCurrentQuestion - 1, answer_id);
        checks.set(intCurrentQuestion - 1,
                questions.get(intCurrentQuestion - 1).answer_id == answer_id);
    }

    public Boolean boolCardsetComplete () {
        return questions != null;
    }

    private ArrayList<Integer> getRandomNumbers (int count, int range, int exceptID) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        ArrayList<Integer> nums = new ArrayList<Integer>();
        for (int i = 0; i < range; i++)
            if (i != exceptID) nums.add(i);
        for (int i = 0; i < count; i++) {
            int j = ran.nextInt(nums.size());
            res.add(nums.get(j));
            nums.remove(j);
        }
        return res;
    }


}
