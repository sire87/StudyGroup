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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class SearchFragment extends Fragment implements View.OnClickListener {

    String mUID;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private StudyGroupAdapter studyGroupAdapter;
    private ArrayList<StudyGroup> studyGroupList;
    private Button filterButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filterButton = (Button) getView().findViewById(R.id.btn_filter);
        filterButton.setOnClickListener(this);

        // firebase: get user info
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mUID = user.getUid();

        // set up recycler view
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        studyGroupList = new ArrayList<StudyGroup>();
        studyGroupAdapter = new StudyGroupAdapter(getContext(), studyGroupList, false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(studyGroupAdapter);

        // get and display data
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                studyGroupList.clear();

                DataSnapshot studyGroupsSnapshot = dataSnapshot.child("groups");
                Iterable<DataSnapshot> studyGroupsChildrenSnapshot = studyGroupsSnapshot.getChildren();

                for (DataSnapshot studyGroup : studyGroupsChildrenSnapshot) {
                    StudyGroup grp = studyGroup.getValue(StudyGroup.class);
                    if (grp != null) {
                        if (checkGroup(grp)) studyGroupList.add(grp);
                    }
                }

                Collections.reverse(studyGroupList);
                studyGroupAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO
            }
        });

        // set options on click listener
        ((ImageView) getActivity().findViewById(R.id.toolbar_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_sort, popupMenu.getMenu());

                ViewParent viewParent = view.getParent();
                View parentView = (View) viewParent;

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.action_sort_group_event_date:
                                Toast.makeText(getContext(), "Sorting by event date", Toast.LENGTH_SHORT).show();

                                // get and display data
                                DatabaseReference mRefEvent = FirebaseDatabase.getInstance().getReference();
                                Query mQuery = mRefEvent.child("groups").orderByChild("dateTo");

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
                                        // TODO
                                    }
                                });

                                return true;

                            case R.id.action_sort_group_creation_date:
                                Toast.makeText(getContext(), "Sorting by creation date", Toast.LENGTH_SHORT).show();

                                // get and display data
                                DatabaseReference mRefCreation = FirebaseDatabase.getInstance().getReference();
                                mRefCreation.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        studyGroupList.clear();

                                        DataSnapshot studyGroupsSnapshot = dataSnapshot.child("groups");
                                        Iterable<DataSnapshot> studyGroupsChildrenSnapshot = studyGroupsSnapshot.getChildren();

                                        for (DataSnapshot studyGroup : studyGroupsChildrenSnapshot) {
                                            StudyGroup grp = studyGroup.getValue(StudyGroup.class);
                                            if (grp != null) {
                                                if (checkGroup(grp)) studyGroupList.add(grp);
                                            }
                                        }

                                        Collections.reverse(studyGroupList);
                                        studyGroupAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // TODO
                                    }
                                });

                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private boolean checkGroup(StudyGroup studyGroup) {

        // check if user is already participating in a study group
        ArrayList<Participant> participants = studyGroup.getParticipants();
        if (participants != null) {
            for (int i = 0; i < participants.size(); i++) {
                Participant p = participants.get(i);
                String uid = p.getUid();
                if (uid.equals(mUID)) return false;
            }
        }

        // check if group is empty
        if (studyGroup.getParticipantCount() == 0) {
            return false;
        }

        // check if group is not full yet
        if (studyGroup.getParticipantCount() >= studyGroup.getParticipantsMax()) {
            return false;
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
            return true;
        } else {
            return false;
        }
    }

    private boolean checkString(StudyGroup studyGroup, String string) {
        if (studyGroup.getGroupName().contains(string)) {
            return true;
        } else if (studyGroup.getGroupDetails().contains(string)) {
            return true;
        } else if (studyGroup.getLocation().contains(string)) {
            return true;
        } else if (studyGroup.getLocationDetail().contains(string)) {
            return true;
        } else if (studyGroup.getDateFrom().contains(string)) {
            return true;
        } else if (studyGroup.getDateTo().contains(string)) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        EditText filterText = (EditText) getView().findViewById(R.id.edit_filter);
        final String filterString = filterText.getText().toString();

        String buttonTxt = filterButton.getText().toString();
        String buttonApplyTxt = getResources().getString(R.string.search_filter_apply);
        String buttonClearTxt = getResources().getString(R.string.search_filter_clear);

        if (buttonTxt.equals(buttonApplyTxt) && !filterString.equals("")) {

            filterButton.setText(buttonClearTxt);
            filterButton.setBackgroundColor(getResources().getColor(R.color.colorError));
            filterText.clearFocus();
            filterText.setFocusable(false);

            // get and display data
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    studyGroupList.clear();

                    DataSnapshot studyGroupsSnapshot = dataSnapshot.child("groups");
                    Iterable<DataSnapshot> studyGroupsChildrenSnapshot = studyGroupsSnapshot.getChildren();

                    for (DataSnapshot studyGroup : studyGroupsChildrenSnapshot) {
                        StudyGroup grp = studyGroup.getValue(StudyGroup.class);
                        if (grp != null) {
                            if (checkGroup(grp) && checkString(grp, filterString))
                                studyGroupList.add(grp);
                        }
                    }

                    studyGroupAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // TODO
                }
            });

        } else if (buttonTxt.equals(buttonClearTxt)) {

            filterButton.setText(buttonApplyTxt);
            filterButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            filterText.setText("");
            filterText.setFocusable(true);
            filterText.setFocusableInTouchMode(true);

            // get and display data
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    studyGroupList.clear();

                    DataSnapshot studyGroupsSnapshot = dataSnapshot.child("groups");
                    Iterable<DataSnapshot> studyGroupsChildrenSnapshot = studyGroupsSnapshot.getChildren();

                    for (DataSnapshot studyGroup : studyGroupsChildrenSnapshot) {
                        StudyGroup grp = studyGroup.getValue(StudyGroup.class);
                        if (grp != null) {
                            if (checkGroup(grp))
                                studyGroupList.add(grp);
                        }
                    }

                    studyGroupAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // TODO
                }
            });

        }
    }
}