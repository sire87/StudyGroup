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
        String email = user.getEmail();

        // display user info
        TextView userEmail = (TextView) getView().findViewById(R.id.info_email);
        userEmail.setText(email);
        TextView userEmailVerification = (TextView) getView().findViewById(R.id.info_email_verification);
        TextView userDoEmailVerification = (TextView) getView().findViewById(R.id.info_do_email_verification);
        if (eMailVerified) {
            userDoEmailVerification.setVisibility(View.GONE);
        } else {
            userEmailVerification.setText(R.string.profile_txt_not_verified);
            userEmailVerification.setTextColor(getResources().getColor(R.color.colorError, null));
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
                getActivity().finish();
            }
        }
    }
}
