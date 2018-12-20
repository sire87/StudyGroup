package at.risingr.studygroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // firebase: get user info
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        boolean eMailVerified = user.isEmailVerified();
        String uid = user.getUid();
        String email = user.getEmail();
        String name = user.getDisplayName();

        // set default username
        if (name == null) {
            String[] emailSplit = email.split("@");
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(emailSplit[0])
                    .build();
            user.updateProfile(profileUpdates);
            name = user.getDisplayName();
            mDatabase.child("users").child(uid).child("name").setValue(name);
        }

        // display user info
        TextView userName = (TextView) getView().findViewById(R.id.info_username);
        userName.setText("User: " + name);
        TextView userEmail = (TextView) getView().findViewById(R.id.info_email);
        userEmail.setText("Email: " + email);
        TextView userEmailVerification = (TextView) getView().findViewById(R.id.info_email_verification);
        if (eMailVerified) {
            userEmailVerification.setText("*verified*");
            userEmailVerification.setTextColor(getResources().getColor(R.color.colorAccent, null));
        } else {
            userEmailVerification.setText("*NOT verified*");
            userEmailVerification.setTextColor(getResources().getColor(R.color.colorError, null));
            TextView userDoEmailVerification = (TextView) getView().findViewById(R.id.info_do_email_verification);
            userDoEmailVerification.setText("Please verify your email to use Study Group Manager.");
            userDoEmailVerification.setTextColor(getResources().getColor(R.color.colorError, null));
            userDoEmailVerification.setVisibility(View.VISIBLE);
        }

        // set on click listeners
        Button btnLogOut = getView().findViewById(R.id.button_sign_out);
        btnLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.button_sign_out) {
            mAuth.signOut();
            if (mAuth.getCurrentUser() == null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}
