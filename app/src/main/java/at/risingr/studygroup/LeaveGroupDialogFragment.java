package at.risingr.studygroup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class LeaveGroupDialogFragment extends DialogFragment {

    LeaveGroupDialogListener mListener;
    StudyGroup studyGroup;

    public LeaveGroupDialogFragment() {
        super();
    }

    public void setListener(LeaveGroupDialogListener listener) {
        mListener = listener;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Leave study group");
        builder.setMessage("Do you really want to leave study group: " + this.studyGroup.getGroupName());
        builder.setPositiveButton("Leave study group", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(LeaveGroupDialogFragment.this);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogNegativeClick(LeaveGroupDialogFragment.this);
            }
        });

        return builder.create();
    }

    public interface LeaveGroupDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }
}