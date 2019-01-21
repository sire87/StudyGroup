package at.risingr.studygroup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private EditText editText;
    private String currentDateString;

    public void setEditText(EditText v) {

        this.editText = v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        this.currentDateString = String.format(Locale.ENGLISH, "%d-%02d-%02d", currentYear, currentMonth + 1, currentDay);

        // create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, currentYear, currentMonth, currentDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // prevent picking past dates
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        String dateString = String.format(Locale.ENGLISH, "%d-%02d-%02d", year, month + 1, day);

        int comparison = dateString.compareTo(currentDateString);
        if (comparison < 0) {
            dateString = currentDateString;
        }

        this.editText.setText(dateString);

        if (this.editText.getId() == R.id.edit_date_from) {
            EditText editText2 = getActivity().findViewById(R.id.edit_date_to);
            editText2.setText(dateString);
        } else if (this.editText.getId() == R.id.edit_date_to) {
            EditText editText2 = getActivity().findViewById(R.id.edit_date_from);
            String dateFrom = editText2.getText().toString();
            String dateTo = this.editText.getText().toString();
            int comparisonDate = dateTo.compareTo(dateFrom);
            if (comparisonDate < 0) {
                editText2.setText(dateString);
            }
        }
    }
}
