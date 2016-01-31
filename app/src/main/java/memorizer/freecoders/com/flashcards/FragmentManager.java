package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

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

    public static int intUIState = Constants.UI_STATE_MAIN_MENU;

    public static PlayersInfoFragment playersInfoFragment = new PlayersInfoFragment();
    private static Fragment currentFragment;
    public static FlashCardFragment currentFlashCardFragment;
    public static MainMenuFragment mainMenuFragment;
    public static UserProfileFragment userProfileFragment;
    public static GameOverFragment gameOverFragment;
    public Boolean boolAvatarChosen = false;

    private static Gson gson = new Gson();

    public static final void showGamePlayFragments (Boolean boolConfigurationChange, int state) {
        hideMainMenu();
        intUIState = state;
        showPlayersInfo(boolConfigurationChange);
        if (boolConfigurationChange)
            showFragment(FlashCardFragment.cloneFragment(currentFlashCardFragment) , null);
    }

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

    public static final void showPlayersInfo (Boolean boolConfigurationChange) {
        if ((playersInfoFragment == null) || boolConfigurationChange)
            playersInfoFragment = new PlayersInfoFragment();

        if (!playersInfoFragment.isAdded()) {
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .add(R.id.fragment_players_info_container, playersInfoFragment).commit();
            Multicards.getMainActivity().getFragmentManager().executePendingTransactions();
        }
    }

    public static final void hidePlayersInfo () {
        if ((playersInfoFragment != null) && (playersInfoFragment.isAdded()))
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .remove(playersInfoFragment).commit();
    }

    public static final void showGameOverFragment (String strCardsetID,
            GameOverMessage gameOverMessage, Boolean boolConfigurationChange) {
        hidePlayersInfo();
        hideCurrentFlashcardFragment();

        Intent intent = new Intent(Multicards.getMainActivity(), GameOverActivity.class);
        intent.putExtra(Constants.INTENT_META_SET_ID, strCardsetID);

        if (intUIState == Constants.UI_STATE_MULTIPLAYER_MODE)
            intent.putExtra(Constants.INTENT_META_GAME_TYPE,
                    GameOverActivity.INT_GAME_TYPE_MULTIPLAYER);
        else if (intUIState == Constants.UI_STATE_TRAIN_MODE)
            intent.putExtra(Constants.INTENT_META_GAME_TYPE,
                    GameOverActivity.INT_GAME_TYPE_SINGLEPLAYER);

        if (gameOverMessage != null)
            intent.putExtra(Constants.INTENT_META_GAMEOVER_MESSAGE, gson.toJson(gameOverMessage));

        Multicards.getMainActivity().startActivity(intent);

        intUIState = Constants.UI_STATE_GAME_OVER;
    }

    public static final void hideGameOverFragment () {
        if (Multicards.getGameOverActivity() != null)
            Multicards.getGameOverActivity().finish();
    }

    public static final void hideMainMenu () {
        if ((mainMenuFragment != null) && (mainMenuFragment.isAdded()))
            Multicards.getMainActivity().getFragmentManager().beginTransaction().
                    remove(mainMenuFragment).commit();
    }

    public static final void hideCardsetPickerActivity () {
        if (Multicards.getCardsetPickerActivity() != null)
            Multicards.getCardsetPickerActivity().finish();
    }

    public static final void returnToMainMenu (Boolean boolConfigurationChange) {
        showMainMenu(boolConfigurationChange);
        hidePlayersInfo();
        hideCurrentFlashcardFragment();
        hideUserProfileFragment();
        hideGameOverFragment();
        intUIState = Constants.UI_STATE_MAIN_MENU;
    }

    public static final void showMainMenu (Boolean boolConfigurationChange) {
        if ((mainMenuFragment == null) || boolConfigurationChange)
            mainMenuFragment = new MainMenuFragment();
        if (!mainMenuFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .add(R.id.fragment_flashcard_container, mainMenuFragment).commit();
    }

    public static final void showUserProfileFragment (Boolean boolConfigurationChange) {
        hidePlayersInfo();
        hideCurrentFlashcardFragment();
        hideMainMenu();

        if ((userProfileFragment == null) || boolConfigurationChange) {
            userProfileFragment = new UserProfileFragment();
        }

        if (!userProfileFragment.isAdded())
            Multicards.getMainActivity().getFragmentManager().beginTransaction()
                    .add(R.id.fragment_flashcard_container, userProfileFragment).commit();
        intUIState = Constants.UI_STATE_SETTINGS;
    }

    public static final void hideUserProfileFragment() {
        if ((userProfileFragment != null) && (userProfileFragment.isAdded()))
            Multicards.getMainActivity().getFragmentManager().beginTransaction().
                    remove(userProfileFragment).commit();
    }

}
