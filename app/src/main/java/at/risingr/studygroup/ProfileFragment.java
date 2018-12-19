package at.risingr.studygroup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean eMailVerified;
    private String uid;
    private String email;
    private String name;
    private Uri photoUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // firebase: get user info
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        eMailVerified = user.isEmailVerified();
        uid = user.getUid();
        email = user.getEmail();
        name = user.getDisplayName();
        if (name == null) {
            String[] emailSplit = email.split("@");
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(emailSplit[0])
                    .build();
            user.updateProfile(profileUpdates);
        }
        photoUrl = user.getPhotoUrl();

        // display user info
        // TODO SIR

        // set on click listeners
        Button btnLogOut = getView().findViewById(R.id.button_sign_out);
        btnLogOut.setOnClickListener(this);
        Button btnSaveChanges = getView().findViewById(R.id.buttonSave);
        btnSaveChanges.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.button_sign_out) {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else if (i == R.id.buttonSave) {
            Toast.makeText(getActivity(), "Changes saved.", Toast.LENGTH_SHORT).show();
            // TODO SIR
        }

    }
}
