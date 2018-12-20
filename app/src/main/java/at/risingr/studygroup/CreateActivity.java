package at.risingr.studygroup;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        findViewById(R.id.edit_date_from).setOnClickListener(this);
        findViewById(R.id.edit_date_to).setOnClickListener(this);
        findViewById(R.id.edit_time_from).setOnClickListener(this);
        findViewById(R.id.edit_time_to).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        if (i == R.id.edit_date_from) {
            showDatePickerDialog(v);
        } else if (i == R.id.edit_date_to) {
            showDatePickerDialog(v);
        } else if (i == R.id.edit_time_from) {
            showTimePickerDialog(v);
        } else if (i == R.id.edit_time_to) {
            showTimePickerDialog(v);
        }
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
}
