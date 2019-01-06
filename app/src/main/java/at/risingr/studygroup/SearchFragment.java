package at.risingr.studygroup;

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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private StudyGroupAdapter studyGroupAdapter;
    private ArrayList<StudyGroup> studyGroupList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO SIR
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        studyGroupList = new ArrayList<StudyGroup>();
        studyGroupAdapter = new StudyGroupAdapter(getContext(), studyGroupList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(studyGroupAdapter);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot studyGroupsSnapshot = dataSnapshot.child("groups");
                Iterable<DataSnapshot> studyGroupsChildrenSnapshot = studyGroupsSnapshot.getChildren();

                for (DataSnapshot studyGroup : studyGroupsChildrenSnapshot) {
                    StudyGroup grp = studyGroup.getValue(StudyGroup.class);
                    Toast.makeText(getContext(), grp.getGroupName(), Toast.LENGTH_SHORT).show();
                    studyGroupList.add(grp);
                }

                studyGroupAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        // TODO SIR
    }
}
