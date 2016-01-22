package memorizer.freecoders.com.flashcards;

import android.app.Fragment;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.fragments.FlashCardFragment;
import memorizer.freecoders.com.flashcards.fragments.GameOverFragment;
import memorizer.freecoders.com.flashcards.fragments.MainMenuFragment;
import memorizer.freecoders.com.flashcards.fragments.PlayersInfoFragment;
import memorizer.freecoders.com.flashcards.fragments.UserProfileFragment;
import memorizer.freecoders.com.flashcards.json.GameOverMessage;

/**
 * Created by alex-mac on 27.12.15.
 */

public class FragmentManager {

    private static String LOG_TAG = "FragmentManager";

    public static PlayersInfoFragment playersInfoFragment = new PlayersInfoFragment();
    private static Fragment currentFragment;
    public static FlashCardFragment currentFlashCardFragment;
    public static MainMenuFragment mainMenuFragment;
    public static UserProfileFragment userProfileFragment;
    public static GameOverFragment gameOverFragment;
    public Boolean boolAvatarChosen = false;

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

    public static final void showGameOverFragment (String strCardsetID,
                                                   GameOverMessage gameOverMessage) {
        hidePlayersInfo();
        hideCurrentFlashcardFragment();

        if (gameOverFragment == null) {
            gameOverFragment = new GameOverFragment();
            gameOverFragment.setCardsetID(strCardsetID);
        }

        if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_MULTIPLAYER_MODE)
            gameOverFragment.INT_GAME_TYPE = GameOverFragment.INT_GAME_TYPE_MULTIPLAYER;
        else if (Multicards.getMainActivity().intUIState == Constants.UI_STATE_TRAIN_MODE)
            gameOverFragment.INT_GAME_TYPE = GameOverFragment.INT_GAME_TYPE_SINGLEPLAYER;

        if (!gameOverFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .add(R.id.fragment_flashcard_container, gameOverFragment).commit();
        Multicards.getMainActivity().intUIState = Constants.UI_STATE_GAME_OVER;

        Multicards.getMainActivity().getFragmentManager().executePendingTransactions();

        if (gameOverMessage != null)
            gameOverFragment.setGameOverMessage(gameOverMessage);
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

    public static final void showUserProfileFragment () {
        hidePlayersInfo();
        hideCurrentFlashcardFragment();
        hideMainMenu();

        if (userProfileFragment == null) {
            userProfileFragment = new UserProfileFragment();
        }

        if (!userProfileFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .add(R.id.fragment_flashcard_container, userProfileFragment).commit();
        Multicards.getMainActivity().intUIState = Constants.UI_STATE_SETTINGS;
    }

    public static final void hideUserProfileFragment() {
        if (userProfileFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction().
                    remove(userProfileFragment).commit();
    }

}
