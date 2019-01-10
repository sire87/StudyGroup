package at.risingr.studygroup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, View.OnClickListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private final static String TAG = "Login";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null; // TODO delete afterwards
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mFeedbackView;
    // firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public void onStart() {
        super.onStart();
        // check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        redirectToMainActivity(currentUser);
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

    public void onClick(View v) {
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

    private void createAccount(String email, String password) {

        Log.d(TAG, "create account: " + email);

        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "create account: success");
                    Toast.makeText(LoginActivity.this, "Authentication (registration) successful.", Toast.LENGTH_SHORT).show();
                    // send verification email
                    sendEmailVerification();
                    updateUI();
                    // add new user entry
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String uid = firebaseUser.getUid();
                    String email = firebaseUser.getEmail();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(uid).child("email").setValue(email);
                } else {
                    // if sign in fails, display message to the user
                    Log.w(TAG, "create account: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication (registration) failed.", Toast.LENGTH_SHORT).show();
                    mFeedbackView.setText(task.getException().getMessage());
                    mFeedbackView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void signIn(String email, String password) {

        Log.d(TAG, "sign in: " + email);

        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "sign in: success");
                    Toast.makeText(LoginActivity.this, "Authentication (sign in) successful.", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    redirectToMainActivity(user);
                } else {
                    // if sign in fails, display message to user
                    Log.w(TAG, "sign in: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication (sign in) failed.", Toast.LENGTH_SHORT).show();
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

    // TODO create corresponding view
    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "send password reset: success");
                    Toast.makeText(LoginActivity.this, "Password reset sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "send password reset: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                    mFeedbackView.setText(task.getException().getMessage());
                    mFeedbackView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private boolean validateForm() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFeedbackView = (TextView) findViewById(R.id.info_feedback);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        // populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        // TODO SIR: insert dummy credentials in email and password views
        String[] credentials = DUMMY_CREDENTIALS[0].split(":");
        mEmailView.setText(credentials[0]);
        mPasswordView.setText(credentials[1] + "salty");

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // set on click listeners
        findViewById(R.id.button_sign_in).setOnClickListener(this);
        findViewById(R.id.button_forgot_password).setOnClickListener(this);
        findViewById(R.id.button_register).setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                // finish();
                // TODO SIR: switch to main activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

