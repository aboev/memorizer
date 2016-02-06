package memorizer.freecoders.com.memo_test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import memorizer.freecoders.com.flashcards.MainActivity;
import memorizer.freecoders.com.flashcards.classes.FlashCard;
import memorizer.freecoders.com.flashcards.classes.GameplayData;
import memorizer.freecoders.com.flashcards.common.Multicards;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void testQuestionData() throws Exception {
        ArrayList<String> terms = new ArrayList<String>();
        ArrayList<String> definitions = new ArrayList<String>();
        String q1 = "1+1="; String a1 = "2";
        String q2 = "2+0="; String a2 = "2";
        String q3 = "0+2="; String a3 = "2";
        String q4 = "2+2="; String a4 = "4";
        terms.add(q1); definitions.add(a1);
        terms.add(q2); definitions.add(a2);
        terms.add(q3); definitions.add(a3);
        terms.add(q4); definitions.add(a4);
        GameplayData.QuestionData questionData = new GameplayData.QuestionData(terms, definitions);

        assertEquals(2, questionData.udefinitions.size());

    }

    @Test
    public void testGameplayData() throws Exception {
        GameplayData gameplayData = new GameplayData(null, GameplayData.INT_SINGLEPLAYER);

        ArrayList<String> terms = new ArrayList<String>();
        ArrayList<String> definitions = new ArrayList<String>();
        terms.add("1+1="); definitions.add("2");
        terms.add("2+0="); definitions.add("2");
        terms.add("0+2="); definitions.add("2");
        terms.add("2+2="); definitions.add("4");

        GameplayData.QuestionData questionData = new GameplayData.QuestionData(terms, definitions);
        ArrayList<FlashCard> questions = gameplayData.makeQuestions(questionData);

        for (int i = 0; i < questions.size(); i++) {
            HashSet<String> set = new HashSet<String>();
            FlashCard question = questions.get(i);
            for (int j = 0; j < question.options.size(); j++) {
                assertFalse(set.contains(question.options.get(j)));
                set.add(question.options.get(j));
            }
        }

        assertEquals(2, questions.get(0).options.size());
    }


}