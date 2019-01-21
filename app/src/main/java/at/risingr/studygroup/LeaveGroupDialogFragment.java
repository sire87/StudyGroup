package at.risingr.studygroup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class LeaveGroupDialogFragment extends DialogFragment {

    public LeaveGroupDialogListener mListener;
    public StudyGroup studyGroup;

    public LeaveGroupDialogFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Leave study group");
        builder.setMessage("Do you really want to leave this study group:\n" +
                this.studyGroup.getGroupName());
        builder.setPositiveButton("Leave study group", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogLeaveClick(LeaveGroupDialogFragment.this);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogLeaveCancelClick(LeaveGroupDialogFragment.this);
            }
        });
        return builder.create();
    }

    public void setListener(LeaveGroupDialogListener listener) {
        mListener = listener;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    public interface LeaveGroupDialogListener {
        public void onDialogLeaveClick(DialogFragment dialog);

        public void onDialogLeaveCancelClick(DialogFragment dialog);
    }
}