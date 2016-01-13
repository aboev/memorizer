package memorizer.freecoders.com.flashcards;

import android.app.Fragment;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.fragments.FlashCardFragment;
import memorizer.freecoders.com.flashcards.fragments.GameOverFragment;
import memorizer.freecoders.com.flashcards.fragments.MainMenuFragment;
import memorizer.freecoders.com.flashcards.fragments.PlayersInfoFragment;

/**
 * Created by alex-mac on 27.12.15.
 */

public class FragmentManager {

    private static String LOG_TAG = "FragmentManager";

    public static PlayersInfoFragment playersInfoFragment = new PlayersInfoFragment();
    private static Fragment currentFragment;
    public static FlashCardFragment currentFlashCardFragment;
    public static MainMenuFragment mainMenuFragment;

    public static final void showFragment (Fragment newFragment, Integer intTransitionType) {
        if (intTransitionType == null)
            Multicards.getMainActivity().getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_flashcard_container, newFragment)
                    .commit();
        else if (intTransitionType == Constants.ANIMATION_FLIP)
            Multicards.getMainActivity().getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.flip_right_in, R.anim.flip_right_out,
                            R.anim.flip_left_in, R.anim.flip_left_out)
                    .replace(R.id.fragment_flashcard_container, newFragment)
                    .commit();

        currentFragment = newFragment;

        if (newFragment instanceof FlashCardFragment)
            currentFlashCardFragment = (FlashCardFragment) newFragment;
    }

    public static final void hideCurrentFlashcardFragment () {
        if ((currentFlashCardFragment != null) && (currentFlashCardFragment.isAdded())) {
            Multicards.getMainActivity().getFragmentManager().beginTransaction().
                    remove(currentFlashCardFragment).commit();
            currentFlashCardFragment = null;
        }
    }

    public static final void showPlayersInfo () {
        if (!playersInfoFragment.isAdded()) {
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .add(R.id.fragment_players_info_container, playersInfoFragment).commit();
            Multicards.getMainActivity().getFragmentManager().executePendingTransactions();
        }
    }

    public static final void hidePlayersInfo () {
        if (playersInfoFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .remove(playersInfoFragment).commit();
    }

    public static final void showGameOverFragment (String strCardsetID) {
        hidePlayersInfo();
        hideCurrentFlashcardFragment();

        if (Multicards.getMainActivity().gameOverFragment == null) {
            Multicards.getMainActivity().gameOverFragment = new GameOverFragment();
            Multicards.getMainActivity().gameOverFragment.setCardsetID(strCardsetID);
        }

        if (!Multicards.getMainActivity().gameOverFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .add(R.id.fragment_flashcard_container,
                            Multicards.getMainActivity().gameOverFragment).commit();
        Multicards.getMainActivity().intUIState = Constants.UI_STATE_GAME_OVER;
    }

    public static final void hideMainMenu () {
        if (mainMenuFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction().
                    remove(mainMenuFragment).commit();
    }

    public static final void hideCardsetPickerActivity () {
        if (Multicards.getCardsetPickerActivity() != null)
            Multicards.getCardsetPickerActivity().finish();
    }

    public static final void returnToMainMenu () {
    }

    public static final void showMainMenu () {
        if (mainMenuFragment == null)
            mainMenuFragment = new MainMenuFragment();
        if (!mainMenuFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .add(R.id.fragment_flashcard_container, mainMenuFragment).commit();
    }

}
