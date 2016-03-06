package memorizer.freecoders.com.flashcards.utils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.GameplayManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.classes.GameplayData;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.dao.Cardset;
import memorizer.freecoders.com.flashcards.json.BonusDescriptor;
import memorizer.freecoders.com.flashcards.json.GameOverMessage;
import memorizer.freecoders.com.flashcards.json.ServerInfo;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.network.StringRequest;

/**
 * Created by alex-mac on 06.01.16.
 */
public class Utils {
    private static String LOG_TAG = "Utils";
    private static PrettyTime pTime = new PrettyTime();

    public static final String extractOpponentSocketID(HashMap<String, UserDetails> map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String strSocketID = (String) pair.getKey();
            if (!strSocketID.equals(Multicards.getPreferences().strSocketID))
                return strSocketID;
            it.remove();
        }
        return null;
    }

    public static final UserDetails extractOpponentProfile(HashMap<String, UserDetails> map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String strSocketID = (String) pair.getKey();
            UserDetails userDetails = (UserDetails) pair.getValue();
            if ((!strSocketID.equals(Multicards.getPreferences().strSocketID)) &&
                    (userDetails.name != null) && (!userDetails.name.isEmpty()))
                return userDetails;
            it.remove();
        }
        return null;
    }

    public static final Integer extractOpponentScore(HashMap<String, Integer> map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String strSocketID = (String) pair.getKey();
            Integer userScore = (Integer) pair.getValue();
            if ((!strSocketID.equals(Multicards.getPreferences().strSocketID)))
                return userScore;
            it.remove();
        }
        return null;
    }

    public final static class AvatarListener implements ImageLoader.ImageListener {
        ImageView imageView;

        public AvatarListener(ImageView imgView) {
            this.imageView = imgView;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
        }

        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
            if (response.getBitmap() != null) {
                imageView.setImageResource(0);
                imageView.setImageBitmap(response.getBitmap());
            }
        }
    }

    public final static String getLocale () {
        String lang = Locale.getDefault().getISO3Language().substring(0, 2);
        return lang;
    }


    public final static HashMap sortHashByValues(HashMap map) {
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

    public final static void checkLatestVersion () {
        ServerInterface.getServerInfoRequest(
                new Response.Listener<ServerInfo>() {
                    @Override
                    public void onResponse(ServerInfo response) {
                        Gson gson = new Gson();
                        Log.d(LOG_TAG, "Server info " + gson.toJson(response));
                        String strServerInfo = gson.toJson(response);
                        if (Multicards.getPreferences().strServerInfo.equals(strServerInfo)) return;
                        PackageInfo pInfo = null;
                        try {
                            pInfo = Multicards.getMainActivity().getPackageManager().getPackageInfo(
                                    Multicards.getMainActivity().getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                        }
                        if ((response.latest_ver != null) && (response.latest_apk != null) &&
                                (response.min_client != null) && pInfo != null) {
                            int intLatestAPKVersion = Integer.valueOf(response.latest_ver);
                            int intMinClientVersion = Integer.valueOf(response.min_client);


                            String strMessage = "";
                            if ((response.update_comment != null) &&
                                    (Utils.getLocaleString(response.update_comment) != null))
                                strMessage = Utils.getLocaleString(response.update_comment);

                            if (intMinClientVersion > pInfo.versionCode)
                                InputDialogInterface.showUpdateDialog(true, strMessage, response);
                            else if (intLatestAPKVersion > pInfo.versionCode)
                                InputDialogInterface.showUpdateDialog(false, strMessage, response);
                        }
                    }
                }, null);
    }

    public final static void OpenPlayMarketPage() {
        final String appPackageName = Multicards.getMainActivity().getPackageName();

        try {
            Multicards.getMainActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(Multicards.getMainActivity().getString(R.string.playMarketDetailsLink) +
                        appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            Multicards.getMainActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Multicards.getMainActivity().getString(
                            R.string.playMarketDetailsLinkAlt) + appPackageName)));
        }

    }

    public final static void vibrateLong() {
        Vibrator v = (Vibrator) Multicards.getMainActivity().
                getSystemService(Multicards.getMainActivity().VIBRATOR_SERVICE);
        v.vibrate(300);
    }

    public final static void vibrateShort() {
        Vibrator v = (Vibrator) Multicards.getMainActivity().
                getSystemService(Multicards.getMainActivity().VIBRATOR_SERVICE);
        v.vibrate(50);
    }

    public final static void vibrateSignal() {
        Vibrator v = (Vibrator) Multicards.getMainActivity().
                getSystemService(Multicards.getMainActivity().VIBRATOR_SERVICE);
        v.vibrate(new long[] {0, 300, 300, 300, 300, 300}, -1);
    }

    public final static void refreshPushID(){
        if ((Multicards.getPreferences().strUserID != null) &&
                !Multicards.getPreferences().strUserID.isEmpty() &&
                !Multicards.getPreferences().strPushID.isEmpty() &&
                !Multicards.getPreferences().boolPushIDsent) {
            UserDetails userDetails = new UserDetails();
            userDetails.setNullFields();
            userDetails.pushid = Multicards.getPreferences().strPushID;
            ServerInterface.updateUserDetailsRequest(userDetails, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Multicards.getPreferences().boolPushIDsent = true;
                    Multicards.getPreferences().savePreferences();
                }
            }, null);
        }
    }

    public final static void generateRandomGameplayData(int intGameType){
        String strGID = "quizlet_415";
        Cardset cardset = Multicards.getFlashCardsDAO().fetchCardset(strGID, true);
        if (cardset == null) {
            Log.d(LOG_TAG, "Cardset is empty");
            Multicards.getFlashCardsDAO().importFromWeb(strGID, null, null);
        }
        GameplayData gameplayData = new GameplayData(strGID, intGameType);
        Random random = new Random();
        for (int i = 0; i < gameplayData.questions.size(); i++)
            gameplayData.setAnswer(random.nextInt(4));
        GameplayManager.currentGameplay = gameplayData;
    }

    public final static void showRandomGameoverActivity(final int intGameType){
        final String strGID = "quizlet_415";
        Cardset cardset = Multicards.getFlashCardsDAO().fetchCardset(strGID, true);

        final CallbackInterface callbackInterface = new CallbackInterface() {
            @Override
            public void onResponse(Object obj) {
                GameplayData gameplayData = new GameplayData(strGID, intGameType);
                Random random = new Random();
                for (int i = 0; i < gameplayData.questions.size(); i++) {
                    gameplayData.getNextQuestion();
                    gameplayData.setAnswer(random.nextInt(4));
                }
                GameplayManager.currentGameplay = gameplayData;

                GameOverMessage gameOverMessage = new GameOverMessage();
                gameOverMessage.bonuses = new HashMap<String, ArrayList<BonusDescriptor>>();

                BonusDescriptor bonus = new BonusDescriptor();
                bonus.bonus = 100;
                bonus.bonus_title = new HashMap<String, String>();
                bonus.bonus_title.put("en", "Super bonus");
                bonus.description = new HashMap<String, String>();
                bonus.description.put("en", "Bonus for playing game");
                ArrayList<BonusDescriptor> bList = new ArrayList<BonusDescriptor>();
                bList.add(bonus);
                bList.add(bonus);
                gameOverMessage.bonuses.put(Multicards.getPreferences().strSocketID, bList);

                gameOverMessage.scores_before = new HashMap<String, Integer>();
                gameOverMessage.scores = new HashMap<String, Integer>();
                UserDetails winner = new UserDetails();
                winner.name = "Name";
                winner.socket_id = Multicards.getPreferences().strSocketID;
                gameOverMessage.winner = winner;

                FragmentManager.intUIState = Constants.UI_STATE_MULTIPLAYER_MODE;
                FragmentManager.showGameOverFragment(strGID, gameOverMessage, false);
            }
        };

        if (cardset == null) {
            Log.d(LOG_TAG, "Cardset is empty");
            Multicards.getFlashCardsDAO().importFromWeb(strGID, new CallbackInterface() {
                @Override
                public void onResponse(Object obj) {
                    callbackInterface.onResponse(null);
                }
            }, null);
        } else callbackInterface.onResponse(null);
    }


    public final static void postDelayed (final CallbackInterface callback, Integer intDelay) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(null);
            }
        }, intDelay);
    }

    public final static String getDeviceID () {
        return Settings.Secure.getString(Multicards.getMainActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public final static String[] parseGID (String strGID) {
        return strGID.split("_");
    }

    public final static Long getSetID (String strGID) {
        Long res = null;
        if (parseGID(strGID).length > 1)
            res = Long.valueOf(parseGID(strGID)[1]);
        return res;
    }

    public final static String getPrettyDate (Long time) {
        return pTime.format(new Date(time));
    }

    public final static Boolean arrayContains (String[] array, String value) {
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(value)) {
                return true;
            }
        return false;
    }

    public final static Boolean arrayContains (Integer[] array, Integer value) {
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(value)) {
                return true;
            }
        return false;
    }

    public final static Drawable getDrawable (String strName) {
        Resources resources = Multicards.getMainActivity().getResources();
        final int resourceId = resources.getIdentifier(strName, "drawable",
                Multicards.getMainActivity().getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(resourceId, Multicards.getMainActivity().getTheme());
        } else {
            return resources.getDrawable(resourceId);
        }
    }

    public final static Drawable getCountryFlagByLang (String strLang) {
        if (Constants.countryMap.containsKey(strLang)) {
            String strFilename = Constants.countryMap.get(strLang);
            return getDrawable(strFilename);
        } else
            return  null;
    }

    public final static String getLocaleString (HashMap<String, String> map) {
        String strRes = "";
        if (map != null)
            if (map.containsKey(Utils.getLocale()))
                strRes = map.get(Utils.getLocale());
            else if (map.containsKey(Constants.DEFAULT_LOCALE))
                strRes = map.get(Constants.DEFAULT_LOCALE);
        return strRes;
    }

}
