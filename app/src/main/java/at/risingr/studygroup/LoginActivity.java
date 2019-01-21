package at.risingr.studygroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "Login";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView mFeedbackView;
    // firebase
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        redirectToMainActivity(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mFeedbackView = (TextView) findViewById(R.id.info_feedback);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // set on click listeners
        findViewById(R.id.button_sign_in).setOnClickListener(this);
        findViewById(R.id.button_forgot_password).setOnClickListener(this);
        findViewById(R.id.button_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // first reset feedback text view
        mFeedbackView.setVisibility(View.GONE);

        int i = v.getId();
        if (i == R.id.button_sign_in) {
            signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
        } else if (i == R.id.button_register) {
            createAccount(mEmailView.getText().toString(), mPasswordView.getText().toString());
        } else if (i == R.id.button_forgot_password) {
            if (TextUtils.isEmpty(mEmailView.getText().toString())) {
                mEmailView.setError("e-Mail must not be empty.");
            } else if (!mEmailView.getText().toString().contains("@")) {
                mEmailView.setError("e-Mail format is invalid.");
            } else {
                sendPasswordResetEmail(mEmailView.getText().toString());
            }
        }
    }

    private boolean formIsValid() {
        boolean valid = true;

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();


        if (!email.contains("@")) {
            mEmailView.setError("e-Mail format is invalid.");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("e-Mail must not be empty.");
            valid = false;
        }

        if (password.length() < 8) {
            mPasswordView.setError("Password must be at least 8 characters long.");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Password must not be empty.");
            valid = false;
        }

        return valid;
    }

    private void updateUI() {
        View mFeedbackView = findViewById(R.id.info_feedback);
        mFeedbackView.setVisibility(View.VISIBLE);
    }

    private void redirectToMainActivity(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void createAccount(String email, String password) {

        Log.d(TAG, "create account: " + email);

        if (!formIsValid()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // create account success, update UI with the signed-in user's information
                    Log.d(TAG, "create account: success");
                    Toast.makeText(LoginActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                    // send verification email
                    sendEmailVerification();
                    updateUI();
                    // add new user entry
                    FirebaseUser user = mAuth.getCurrentUser();
                    redirectToMainActivity(user);
                } else {
                    // if create account fails, display message to the user
                    Log.w(TAG, "create account: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    mFeedbackView.setText(task.getException().getMessage());
                    mFeedbackView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void signIn(String email, String password) {

        Log.d(TAG, "sign in: " + email);

        if (!formIsValid()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "sign in: success");
                    Toast.makeText(LoginActivity.this, "Sign in successful.", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    redirectToMainActivity(user);
                } else {
                    // if sign in fails, display message to user
                    Log.w(TAG, "sign in: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Sign in failed.", Toast.LENGTH_SHORT).show();
                    mFeedbackView.setText(task.getException().getMessage());
                    mFeedbackView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "send email: success");
                    Toast.makeText(LoginActivity.this, "Verification email sent to: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "send email: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    mFeedbackView.setText(task.getException().getMessage());
                    mFeedbackView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "send password reset: success");
                    Toast.makeText(LoginActivity.this, "Password reset sent", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "send password reset: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                    mFeedbackView.setText(task.getException().getMessage());
                    mFeedbackView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}

