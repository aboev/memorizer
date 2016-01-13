package memorizer.freecoders.com.flashcards.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.MultipartRequest;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.network.VolleySingleton;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 12.12.15.
 */
public class UserProfileFragment extends Fragment {
    private static String LOG_TAG = "UserProfileFragment";

    private View view;
    private CircleImageView circleImageView;
    private EditText editTextUsername;
    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_userprofile, container, false);
        this.view = view;

        circleImageView = (CircleImageView) view.findViewById(R.id.imageViewAvatar);
        editTextUsername = (EditText) view.findViewById(R.id.editTextUserName);
        saveButton = (Button) view.findViewById(R.id.buttonSave);

        populateView();

        return view;
    }

    private void populateView() {
        if ((Multicards.getPreferences().strAvatar != null)
                &&(!Multicards.getPreferences().strAvatar.isEmpty()))
            Multicards.getAvatarLoader().get(Multicards.getPreferences().strAvatar,
                new Utils.AvatarListener(circleImageView));

        if ((Multicards.getPreferences().strUserName != null)
                &&(!Multicards.getPreferences().strUserName.isEmpty()))
            editTextUsername.setText(Multicards.getPreferences().strUserName);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAvatarPick();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUsername = editTextUsername.getText().toString();
                if ((strUsername != null) && (!strUsername.isEmpty())) {
                    UserDetails userDetails = new UserDetails();
                    userDetails.setNullFields();
                    userDetails.name = strUsername;
                    ServerInterface.updateUserDetailsRequest(userDetails,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    FragmentManager.returnToMainMenu();
                                }
                    }, null);
                }
            }
        });

    }

    public void doAvatarPick(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                        getResources().getString(R.string.alert_select_image)),
                Constants.INTENT_PICK_IMAGE);
    }

    public void uploadAvatar() {
        if ((Multicards.getPreferences().strUserID == null) ||
                (Multicards.getPreferences().strUserID.isEmpty()))
            return;

        if ((Multicards.getPreferences().strSocketID == null) ||
                (Multicards.getPreferences().strSocketID.isEmpty()))
            return;

        HashMap<String, String> headers = new HashMap<String,String>();
        headers.put(Constants.HEADER_USERID, Multicards.getPreferences().strUserID);
        headers.put(Constants.HEADER_SOCKETID, Multicards.getPreferences().strSocketID);
        MultipartRequest uploadRequest = new MultipartRequest(
                Constants.SERVER_URL + Constants.SERVER_PATH_UPLOAD,
                Multicards.getPreferences().strAvatarLocal,
                headers,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, response.toString());
                        try {
                            JSONObject obj = new JSONObject( response);
                            String strAvatarUrl = obj.getString(Constants.RESPONSE_DATA);
                            Multicards.getPreferences().strAvatar = strAvatarUrl;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(LOG_TAG, "Exception " + e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d(LOG_TAG, "Error: " + error.getMessage());
                    }
                }
        );
        VolleySingleton.getInstance(Multicards.getMainActivity()).addToRequestQueue(uploadRequest);
    }

}
