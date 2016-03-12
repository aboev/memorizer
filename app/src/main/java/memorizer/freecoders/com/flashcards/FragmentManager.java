package memorizer.freecoders.com.flashcards;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.fragments.FlashCardFragment;
import memorizer.freecoders.com.flashcards.fragments.MainMenuFragment;
import memorizer.freecoders.com.flashcards.fragments.PlayersInfoFragment;
import memorizer.freecoders.com.flashcards.fragments.UserProfileFragment;
import memorizer.freecoders.com.flashcards.json.GameOverMessage;
import memorizer.freecoders.com.flashcards.network.SocketInterface;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 27.12.15.
 */

public class FragmentManager {

    private static String LOG_TAG = "FragmentManager";

    public static int intUIState = Constants.UI_STATE_MAIN_MENU;
    public static Set<Integer> setUIStates = new HashSet<Integer>();

    public static PlayersInfoFragment playersInfoFragment = new PlayersInfoFragment();
    private static Fragment currentFragment;
    public static FlashCardFragment currentFlashCardFragment;
    public static MainMenuFragment mainMenuFragment;
    public static UserProfileFragment userProfileFragment;
    public Boolean boolAvatarChosen = false;

    private static Gson gson = new Gson();

    public static final void showGamePlayFragments (Boolean boolConfigurationChange, int state) {
        hideMainMenu();
        intUIState = state;
        setUIStates.add(state);
        showPlayersInfo(boolConfigurationChange);
        if (boolConfigurationChange)
            showFragment(FlashCardFragment.cloneFragment(currentFlashCardFragment) , null);
        Multicards.getMainActivity().linearLayoutEmoticons.setVisibility(View.INVISIBLE);
        Multicards.getMainActivity().linearLayoutBottomBar.setVisibility(View.VISIBLE);
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

        hideGameOverFragment();

        Multicards.getMainActivity().startActivity(intent);

        intUIState = Constants.UI_STATE_GAME_OVER;
        setUIStates.add(Constants.UI_STATE_GAME_OVER);
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
        Multicards.getMainActivity().linearLayoutEmoticons.setVisibility(View.GONE);
        Multicards.getMainActivity().linearLayoutBottomBar.setVisibility(View.INVISIBLE);
        intUIState = Constants.UI_STATE_MAIN_MENU;
        setUIStates.remove(Constants.UI_STATE_MULTIPLAYER_MODE);
        setUIStates.remove(Constants.UI_STATE_TRAIN_MODE);
        setUIStates.remove(Constants.UI_STATE_GAME_OVER);
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

    public static final void initEmoticons() {
        if ((Multicards.getMultiplayerInterface().currentGame == null) ||
                (Multicards.getMultiplayerInterface().currentGame.game == null) ||
                (Multicards.getMultiplayerInterface().currentGame.game.profiles == null))
            return;
        final String strOpponentSocketID = Utils.extractOpponentSocketID
                (Multicards.getMultiplayerInterface().currentGame.game.profiles);
        Multicards.getMainActivity().imageViewEmoticon1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocketInterface.emitSendEmoticon(strOpponentSocketID, 1);
                        playersInfoFragment.showEmoticon(false, 1);
                    }
                });
        Multicards.getMainActivity().imageViewEmoticon2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocketInterface.emitSendEmoticon(strOpponentSocketID, 2);
                        playersInfoFragment.showEmoticon(false, 2);
                    }
                });
        Multicards.getMainActivity().imageViewEmoticon3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocketInterface.emitSendEmoticon(strOpponentSocketID, 3);
                        playersInfoFragment.showEmoticon(false, 3);
                    }
                });
        Multicards.getMainActivity().imageViewEmoticon4.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocketInterface.emitSendEmoticon(strOpponentSocketID, 4);
                        playersInfoFragment.showEmoticon(false, 4);
                    }
                });
        Multicards.getMainActivity().imageViewEmoticon5.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocketInterface.emitSendEmoticon(strOpponentSocketID, 5);
                        playersInfoFragment.showEmoticon(false, 5);
                    }
                });
        Multicards.getMainActivity().imageViewEmoticon6.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocketInterface.emitSendEmoticon(strOpponentSocketID, 6);
                        playersInfoFragment.showEmoticon(false, 6);
                    }
                });
    }

    public static final void showOpponentEmoticon (Integer intEmoticonID) {
        playersInfoFragment.showEmoticon(true, intEmoticonID);
    }

}
