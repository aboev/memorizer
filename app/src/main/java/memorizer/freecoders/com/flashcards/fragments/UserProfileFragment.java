package memorizer.freecoders.com.flashcards.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.FragmentManager;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.InputDialogInterface;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.network.MultipartRequest;
import memorizer.freecoders.com.flashcards.network.ServerInterface;
import memorizer.freecoders.com.flashcards.network.SocketInterface;
import memorizer.freecoders.com.flashcards.network.VolleySingleton;
import memorizer.freecoders.com.flashcards.utils.FileUtils;
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
    private ImageView nameFreeImageView;
    private ImageView nameTakenImageView;
    private Boolean boolNameFree = true;

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
        nameTakenImageView = (ImageView) view.findViewById(R.id.imageViewNameTaken);
        nameFreeImageView = (ImageView) view.findViewById(R.id.imageViewNameFree);

        populateView();

        return view;
    }

    private void populateView() {
        loadAvatar();

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
                final String strUsername = editTextUsername.getText().toString();
                if ((strUsername != null) && (!strUsername.isEmpty())) {
                    if (boolNameFree) {
                        UserDetails userDetails = new UserDetails();
                        userDetails.setNullFields();
                        userDetails.name = strUsername;
                        ServerInterface.updateUserDetailsRequest(userDetails,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Multicards.getPreferences().strUserName = strUsername;
                                        Multicards.getPreferences().savePreferences();
                                        FragmentManager.returnToMainMenu(false);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        FragmentManager.returnToMainMenu(false);
                                    }
                                });
                    } else {
                        String strMessage = getResources().getString(R.string.dialog_name_taken);
                        InputDialogInterface.showModalDialog(strMessage);
                    }
                }
            }
        });

        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SocketInterface.socketCheckName(editTextUsername.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SocketInterface.socketCheckName(editTextUsername.getText().toString());

    }

    public void doAvatarPick(){
        if (Build.VERSION.SDK_INT <19){
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getResources().
                    getString(R.string.alert_select_image)), Constants.INTENT_PICK_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            startActivityForResult(Intent.createChooser(intent, getResources().
                    getString(R.string.alert_select_image)), Constants.INTENT_PICK_IMAGE_KITKAT);
        }
    }

    public void uploadAvatar() {
        if ((Multicards.getPreferences().strUserID == null) ||
                (Multicards.getPreferences().strUserID.isEmpty()))
            return;

        if ((Multicards.getPreferences().strSocketID == null) ||
                (Multicards.getPreferences().strSocketID.isEmpty()))
            return;

        String strMessage = getResources().getString(R.string.dialog_uploading_avatar);
        final ProgressDialog progressDialog = ProgressDialog.show(
                Multicards.getMainActivity(), "", strMessage, true);
        progressDialog.setCancelable(true);
        HashMap<String, String> headers = new HashMap<String,String>();
        headers.put(Constants.HEADER_USERID, Multicards.getPreferences().strUserID);
        headers.put(Constants.HEADER_SOCKETID, Multicards.getPreferences().strSocketID);

        File avatarImage = new File(Multicards.getMainActivity().getFilesDir(),
                Constants.FILENAME_AVATAR);
        MultipartRequest uploadRequest = new MultipartRequest(
                Constants.SERVER_URL + Constants.SERVER_PATH_UPLOAD,
                avatarImage,
                headers,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(LOG_TAG, response.toString());
                        try {
                            JSONObject obj = new JSONObject( response);
                            String strAvatarUrl = obj.getString(Constants.RESPONSE_DATA);
                            Multicards.getPreferences().strAvatar = strAvatarUrl;
                            loadAvatar();
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Log.d(LOG_TAG, "Exception " + e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Log.d(LOG_TAG, "Error: " + error.getMessage());
                    }
                }
        );
        VolleySingleton.getInstance(Multicards.getMainActivity()).addToRequestQueue(uploadRequest);
    }

    private void loadAvatar() {
        if ((Multicards.getPreferences().strAvatar != null)
                &&(!Multicards.getPreferences().strAvatar.isEmpty()))
            Multicards.getAvatarLoader().get(Multicards.getPreferences().strAvatar,
                    new Utils.AvatarListener(circleImageView));
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(((requestCode == Constants.INTENT_PICK_IMAGE) || (requestCode == Constants.INTENT_PICK_IMAGE_KITKAT))
                && data != null && data.getData() != null) {
            Uri originalUri = data.getData();

            if (requestCode == Constants.INTENT_PICK_IMAGE_KITKAT) {
                originalUri = data.getData();
                int takeFlags = data.getFlags();
                takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Multicards.getMainActivity().
                        getContentResolver().takePersistableUriPermission(originalUri, takeFlags);
            }

            File dstFile = new File(Multicards.getMainActivity().getFilesDir(),
                    Constants.FILENAME_AVATAR);
            new Crop(originalUri).output(Uri.fromFile(dstFile)).
                    asSquare().start(Multicards.getMainActivity(), this);
        } else if (requestCode == Crop.REQUEST_CROP
                && resultCode == Multicards.getMainActivity().RESULT_OK) {
            Multicards.getPreferences().savePreferences();
            FragmentManager.userProfileFragment.uploadAvatar();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void nameStatus(HashMap<String, Boolean> nameMap) {
        String strCurrentName = editTextUsername.getText().toString();
        if (nameMap.containsKey(strCurrentName)) {
            if (nameMap.get(strCurrentName)) {
                nameFreeImageView.setVisibility(View.VISIBLE);
                nameTakenImageView.setVisibility(View.GONE);
                boolNameFree = true;
                saveButton.setEnabled(true);
                editTextUsername.setTextColor(
                        ContextCompat.getColor(Multicards.getMainActivity(), R.color.colorBlack));
            } else {
                nameFreeImageView.setVisibility(View.GONE);
                nameTakenImageView.setVisibility(View.VISIBLE);
                boolNameFree = false;
                saveButton.setEnabled(false);
                editTextUsername.setTextColor(
                        ContextCompat.getColor(Multicards.getMainActivity(), R.color.colorRed));
            }
        }
    }

}
