package at.risingr.studygroup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class JoinGroupDialogFragment extends DialogFragment implements View.OnClickListener {

    public JoinGroupDialogListener mListener;
    public StudyGroup studyGroup;
    public int knowledge;
    public String comment;

    private EditText editComment;
    private SeekBar seekBarKnowledge;

    public JoinGroupDialogFragment() {
        super();
    }

    public void setListener(JoinGroupDialogListener listener) {
        mListener = listener;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_join, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar_join);
        mToolbar.setTitle(studyGroup.getGroupName());
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        this.seekBarKnowledge = (SeekBar) view.findViewById(R.id.seek_bar_knowledge);
        this.editComment = (EditText) view.findViewById(R.id.edit_comment);

        Button cancelBtn = (Button) view.findViewById(R.id.btn_cancel_join);
        cancelBtn.setOnClickListener(this);
        Button joinBtn = (Button) view.findViewById(R.id.btn_join);
        joinBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_join) {
            this.knowledge = this.seekBarKnowledge.getProgress();
            this.comment = this.editComment.getText().toString();
            mListener.onDialogJoinClick(JoinGroupDialogFragment.this);
            getDialog().dismiss();
        } else if (id == R.id.btn_cancel_join) {
            mListener.onDialogJoinCancelClick(JoinGroupDialogFragment.this);
            getDialog().dismiss();
        }
    }

    public interface JoinGroupDialogListener {
        public void onDialogJoinClick(DialogFragment dialog);

        public void onDialogJoinCancelClick(DialogFragment dialog);
    }
}
