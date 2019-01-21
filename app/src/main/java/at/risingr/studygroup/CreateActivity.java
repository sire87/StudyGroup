package at.risingr.studygroup;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PLACE_PICKER_REQUEST = 1;

    private EditText editGroupName;
    private EditText editGroupDetails;
    private EditText editParticipantsMax;
    private EditText editDateFrom;
    private EditText editDateTo;
    private EditText editTimeFrom;
    private EditText editTimeTo;
    private EditText editLocation;
    private EditText editLocationDetail;
    private EditText editComment;

    private String latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Set back arrow for toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_create);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // get edit texts
        editGroupName = findViewById(R.id.edit_group_name);
        editGroupDetails = findViewById(R.id.edit_group_details);
        editParticipantsMax = findViewById(R.id.edit_participants_max);
        editDateFrom = findViewById(R.id.edit_date_from);
        editDateTo = findViewById(R.id.edit_date_to);
        editTimeFrom = findViewById(R.id.edit_time_from);
        editTimeTo = findViewById(R.id.edit_time_to);
        editLocation = findViewById(R.id.edit_location);
        editLocationDetail = findViewById(R.id.edit_location_detail);
        editComment = findViewById(R.id.edit_comment);

        // get current time values (needed for default values)
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // format current date and time strings
        String dateString = String.format(Locale.ENGLISH, "%d-%02d-%02d", year, month + 1, day);
        String timeStringFrom = String.format(Locale.ENGLISH, "%02d:%02d", hour, minute);
        String timeStringTo = String.format(Locale.ENGLISH, "%02d:%02d", hour + 1, minute);

        // set default values
        editParticipantsMax.setText("4");
        editDateFrom.setText(dateString);
        editDateTo.setText(dateString);
        editTimeFrom.setText(timeStringFrom);
        editTimeTo.setText(timeStringTo);

        // set on click listeners
        findViewById(R.id.btn_participant_incr).setOnClickListener(this);
        findViewById(R.id.btn_participants_decr).setOnClickListener(this);
        findViewById(R.id.edit_date_from).setOnClickListener(this);
        findViewById(R.id.edit_date_to).setOnClickListener(this);
        findViewById(R.id.edit_time_from).setOnClickListener(this);
        findViewById(R.id.edit_time_to).setOnClickListener(this);
        findViewById(R.id.edit_location).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_create).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        if (i == R.id.btn_participant_incr) {
            changeParticipants(false);
        } else if (i == R.id.btn_participants_decr) {
            changeParticipants(true);
        } else if (i == R.id.edit_date_from) {
            showDatePickerDialog(v);
        } else if (i == R.id.edit_date_to) {
            showDatePickerDialog(v);
        } else if (i == R.id.edit_time_from) {
            showTimePickerDialog(v);
        } else if (i == R.id.edit_time_to) {
            showTimePickerDialog(v);
        } else if (i == R.id.edit_location) {
            startPlacePicker();
        } else if (i == R.id.btn_cancel) {
            finish();
        } else if (i == R.id.btn_create) {
            createStudyGroup();
        }
    }

    private void changeParticipants(Boolean decrement) {

        EditText editParticipantsMax = findViewById(R.id.edit_participants_max);
        int participantsMax = Integer.parseInt(editParticipantsMax.getText().toString());

        if (decrement && participantsMax > 2) {
            participantsMax--;
        } else if (!decrement) {
            participantsMax++;
        }

        editParticipantsMax.setText(String.valueOf(participantsMax));
    }

    private void showDatePickerDialog(View v) {

        DialogFragment datePickerFragment = new DatePickerFragment();
        ((DatePickerFragment) datePickerFragment).setEditText((EditText) v);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog(View v) {
        DialogFragment timePickerFragment = new TimePickerFragment();
        ((TimePickerFragment) timePickerFragment).setEditText((EditText) v);
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void startPlacePicker() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String placeName = String.format("%s\n%s", place.getName(), place.getAddress());
                latLng = place.getLatLng().latitude + "," + place.getLatLng().longitude;

                ((EditText) findViewById(R.id.edit_location)).setText(placeName);
            }
        }
    }

    private void createStudyGroup() {

        if (!validateForm()) {
            return;
        }

        // group related
        String groupName = editGroupName.getText().toString();
        String groupDetails = editGroupDetails.getText().toString();
        int participantsMax = Integer.parseInt(editParticipantsMax.getText().toString());
        String dateFrom = editDateFrom.getText().toString();
        String dateTo = editDateTo.getText().toString();
        String timeFrom = editTimeFrom.getText().toString();
        String timeTo = editTimeTo.getText().toString();
        String location = editLocation.getText().toString();
        String locationDetail = editLocationDetail.getText().toString();

        // create participant (i.e. creator)
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        String name = user.getEmail();
        int knowledge = ((SeekBar) findViewById(R.id.seek_bar_knowledge)).getProgress();
        String comment = editComment.getText().toString();
        Participant creator = new Participant(uid, name, comment, knowledge, true);

        // insert study group into database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference grpRef = mDatabase.child("groups").push();
        String grpID = grpRef.getKey();

        // create study group
        StudyGroup grp = new StudyGroup(grpID, groupName, groupDetails, participantsMax, dateFrom, dateTo,
                timeFrom, timeTo, location, locationDetail, latLng, creator);

        grpRef.setValue(grp);

        // give feedback to user and close activity
        Toast.makeText(this, "Study Group " + grpID + " created.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateForm() {

        boolean valid = true;

        if (TextUtils.isEmpty(editGroupName.getText())) {
            valid = false;
            editGroupName.setError("Group name must not be empty.");
        }
        if (TextUtils.isEmpty(editLocation.getText())) {
            editLocation.setError("Location must not be empty.");
            valid = false;
        }

        // TODO check date fields

        return valid;
    }
}