package memorizer.freecoders.com.flashcards.classes;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Card;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.json.MetaItem;

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
    public final static int INT_SINGLEPLAYER = 0;
    public final static int INT_MULTIPLAYER = 1;
    public int intGameType = INT_SINGLEPLAYER;
    private Gson gson = new Gson();

    private Random ran = new Random();

    public GameplayData (String strGID, int intGameType) {
        ArrayList<String> terms = new ArrayList<String>();
        ArrayList<String> definitions = new ArrayList<String>();
        answers = new ArrayList<Integer>();
        checks = new ArrayList<Boolean>();
        questions = new ArrayList<FlashCard>();
        this.intGameType = intGameType;

        if (strGID == null) return;

        Cardset cardset = Multicards.getFlashCardsDAO().fetchCardset(strGID, true);
        this.strGID = strGID;

        ArrayList<Card> cards = Multicards.getFlashCardsDAO().fetchRandomCards(cardset.getId(),
                Constants.GAMEPLAY_QUESTIONS_PER_GAME);
        if ((cards.size() <= Constants.GAMEPLAY_OPTIONS_PER_QUESTION) || cardset == null)
            return;

        for (int i = 0; i < cards.size(); i++) {
            String strTerm = gson.toJson(new MetaItem(cards.get(i).question,null));
            String strDefinition = gson.toJson(new MetaItem(cards.get(i).answer,
                    cards.get(i).getImageDescriptor()));

            if (!cardset.inverted) {
                terms.add(strTerm);
                definitions.add(strDefinition);
            } else {
                terms.add(strDefinition);
                definitions.add(strTerm);
            }
        }
        questions = makeQuestions(new QuestionData(terms, definitions), cardset.has_images);

        intCurrentQuestion = 0;
    }

    public void newServerQuestion (FlashCard question) {
        questions.add(question);
        answers.add(-1);
        checks.add(false);
        intCurrentQuestion = questions.size();
    }

    public ArrayList<FlashCard> makeQuestions (QuestionData questionData, Boolean boolHasImages) {
        ArrayList<String> terms = questionData.terms;
        ArrayList<String> definitions = questionData.udefinitions;
        ArrayList<FlashCard> res = new ArrayList<FlashCard>();
        ArrayList<Integer> termIdList = getRandomNumbers(terms.size(), terms.size(), -1);

        int optionCount = Math.min(Constants.GAMEPLAY_OPTIONS_PER_QUESTION, definitions.size());

        int i = 0;
        while ((i < terms.size()) && (i < Constants.GAMEPLAY_QUESTIONS_PER_GAME)) {
            int termID = termIdList.get(i);
            int answerID = ran.nextInt(optionCount);

            FlashCard question = new FlashCard();
            int except = questionData.termDefinitionMap.get(termID);
            ArrayList<Integer> optionIdList = getRandomNumbers(
                    optionCount - 1, definitions.size(), except);

            for (int j = 0; j < optionIdList.size(); j++) {
                if (j == answerID)
                    question.options_img.add(MetaItem.fromJsonString(
                            questionData.getDefinition(termID)));
                question.options_img.add(MetaItem.fromJsonString(
                        definitions.get(optionIdList.get(j))));
            }

            if (answerID == optionIdList.size())
                question.options_img.add(MetaItem.fromJsonString(
                        questionData.getDefinition(termID)));

            question.question_img = MetaItem.fromJsonString(terms.get(termID));
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
        int count_min = Math.min(count, nums.size());
        for (int i = 0; i < count_min; i++) {
            int j = ran.nextInt(nums.size());
            res.add(nums.get(j));
            nums.remove(j);
        }
        return res;
    }

    public void updateStatistic () {

    }

    public static class QuestionData {
        public ArrayList<String> terms;
        public ArrayList<String> definitions;
        public ArrayList<String> udefinitions;
        public HashMap<Integer, Integer> termDefinitionMap = new HashMap<Integer, Integer>();

        public QuestionData (ArrayList<String> terms, ArrayList<String> definitions) {
            this.terms = terms;
            this.definitions = definitions;
            this.udefinitions = new ArrayList<String>();
            filterUnique();
        }

        private void filterUnique () {
            HashSet<String> set = new HashSet<String>();
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            for (int i = 0; i < terms.size(); i++) {
                if (!set.contains(definitions.get(i))) {
                    udefinitions.add(definitions.get(i));
                    map.put(definitions.get(i), udefinitions.size() - 1);
                    set.add(definitions.get(i));
                }
                termDefinitionMap.put(i, map.get(definitions.get(i)));
            }
        }

        public String getDefinition(int i) {
            return udefinitions.get(termDefinitionMap.get(i));
        }
    }


}
