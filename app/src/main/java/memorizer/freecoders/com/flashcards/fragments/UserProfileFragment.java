package memorizer.freecoders.com.flashcards.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.R;

/**
 * Created by alex-mac on 12.12.15.
 */
public class UserProfileFragment extends Fragment {
    private static String LOG_TAG = "UserProfileFragment";

    private View view;
    private CircleImageView circleImageView;
    private EditText editTextUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_userprofile, container, false);
        this.view = view;

        circleImageView = (CircleImageView) view.findViewById(R.id.imageViewAvatar);
        editTextUsername = (EditText) view.findViewById(R.id.editTextUserName);

        populateView();

        return view;
    }

    private static void populateView() {

    }
}
