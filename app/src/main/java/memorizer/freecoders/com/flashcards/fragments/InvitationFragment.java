package memorizer.freecoders.com.flashcards.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.classes.CallbackInterface;
import memorizer.freecoders.com.flashcards.common.Constants;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.InvitationDescriptor;
import memorizer.freecoders.com.flashcards.json.QCardset;
import memorizer.freecoders.com.flashcards.json.UserDetails;
import memorizer.freecoders.com.flashcards.utils.Utils;

/**
 * Created by alex-mac on 23.01.16.
 */
public class InvitationFragment extends DialogFragment {

    private static String LOG_TAG = "InvitationFragment";

    CircleImageView imageViewAvatar;
    TextView textViewUsername;
    TextView textViewInvitation;
    TextView textViewCardsetName;
    Button buttonOK;
    Button buttonCancel;
    CallbackInterface onClickOK;
    InvitationDescriptor invitation;

    public InvitationFragment() {

    }

    public void setInvitationDetails(InvitationDescriptor invitation) {
        this.invitation = invitation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invitation, container);
        imageViewAvatar = (CircleImageView) view.findViewById(R.id.imageViewAvatar);
        textViewInvitation = (TextView) view.findViewById(R.id.textViewInvitation);
        textViewCardsetName = (TextView) view.findViewById(R.id.textViewCardsetName);
        textViewUsername = (TextView) view.findViewById(R.id.textViewUsername);

        buttonOK = (Button) view.findViewById(R.id.buttonOK);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOK.onResponse(null);
                dismissFragment();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissFragment();
            }
        });

        populateView();

        return view;
    }

    private void populateView () {
        if (invitation == null) return;
        UserDetails user = invitation.user;
        QCardset cardset = invitation.cardset;
        if (user != null) {
            if (invitation.user.avatar != null)
                Multicards.getAvatarLoader().get(invitation.user.avatar,
                        new Utils.AvatarListener(imageViewAvatar));
            if (user.name != null)
                textViewUsername.setText(user.name);
        }
        if ((cardset != null) && (cardset.title != null))
            textViewCardsetName.setText(cardset.title);
    }

    public void setOnClickOKListener (CallbackInterface callback) {
        this.onClickOK = callback;
    }

    public void dismissFragment () {
        Fragment prev = Multicards.getMainActivity().
                getFragmentManager().findFragmentByTag(Constants.TAG_INVITATION_FRAGMENT);
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

}
