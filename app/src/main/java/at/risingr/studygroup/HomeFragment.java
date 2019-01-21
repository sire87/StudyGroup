package at.risingr.studygroup;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

    String mUID;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private StudyGroupAdapter studyGroupAdapter;
    private ArrayList<StudyGroup> studyGroupList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // firebase: get user info
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mUID = user.getUid();

        // set up recycler view
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        studyGroupList = new ArrayList<StudyGroup>();
        studyGroupAdapter = new StudyGroupAdapter(getContext(), studyGroupList, true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(studyGroupAdapter);

        // get and display data
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        Query mQuery = mRef.child("groups").orderByChild("dateTo");

        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                studyGroupList.clear();

                DataSnapshot studyGroupsSnapshot = dataSnapshot;
                Iterable<DataSnapshot> studyGroupsChildrenSnapshot = studyGroupsSnapshot.getChildren();

                for (DataSnapshot studyGroup : studyGroupsChildrenSnapshot) {
                    StudyGroup grp = studyGroup.getValue(StudyGroup.class);
                    if (grp != null) {
                        if (checkGroup(grp)) studyGroupList.add(grp);
                    }
                }

                studyGroupAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private boolean checkGroup(StudyGroup studyGroup) {

        boolean isInGroup = false;
        boolean isInFuture = false;

        // check if user is already participating in a study group
        ArrayList<Participant> participants = studyGroup.getParticipants();
        if (participants != null) {
            for (int i = 0; i < participants.size(); i++) {
                Participant p = participants.get(i);
                String uid = p.getUid();
                if (uid.equals(mUID)) {
                    isInGroup = true;
                }
            }
        }

        // check if end date is still in the future
        String dateTo = studyGroup.getDateTo();
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String dateCurrent = String.format(Locale.ENGLISH, "%d-%02d-%02d", year, month + 1, day);
        int comparison = dateTo.compareTo(dateCurrent);
        if (comparison >= 0) {
            isInFuture = true;
        } else {
            isInFuture = false;
        }
        return (isInGroup && isInFuture);
    }
}