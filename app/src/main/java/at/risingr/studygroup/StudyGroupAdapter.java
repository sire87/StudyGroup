package at.risingr.studygroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;

public class StudyGroupAdapter extends RecyclerView.Adapter<StudyGroupAdapter.MyViewHolder> implements LeaveGroupDialogFragment.LeaveGroupDialogListener {

    private Context mContext;
    private ArrayList<StudyGroup> studyGroups;
    private boolean isHome;
    private String uid;
    private String name;

    public StudyGroupAdapter(Context mContext, ArrayList<StudyGroup> studyGroups, boolean isHome) {
        this.mContext = mContext;
        this.studyGroups = studyGroups;
        this.isHome = isHome;
        // get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        this.uid = user.getUid();
        this.name = user.getEmail();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_group, parent, false);
        itemView.setOnClickListener(new CardItemListener());
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, int position) {
        final StudyGroup studyGroup = studyGroups.get(position);
        viewHolder.grpName.setText(studyGroup.getGroupName());
        viewHolder.grpParticipants.setText(studyGroup.getParticipantCount() + "/" + studyGroup.getParticipantsMax());
        viewHolder.grpTime.setText(studyGroup.getTimeFrom() + " - " + studyGroup.getTimeTo());
        viewHolder.grpDetails.setText(studyGroup.getGroupDetails());
        viewHolder.grpLocation.setText(studyGroup.getLocation());
        viewHolder.grpLocationDetails.setText(studyGroup.getLocationDetail());

        if (studyGroup.getDateTo().equals(studyGroup.getDateFrom())) {
            viewHolder.grpDate.setText(studyGroup.getDateTo());
        } else {
            viewHolder.grpDate.setText(studyGroup.getDateFrom() + " -\n" + studyGroup.getDateTo());
        }

        hideDetails(viewHolder);
        setStars(viewHolder, studyGroup);
        setParticipantsDetails(viewHolder, studyGroup);

        // display location map
        String latLng = studyGroup.getLatLng();
        String url = "https://maps.googleapis.com/maps/api/staticmap?key=AIzaSyBPePir5vHnng0wXHAPqQuelh4mnMb9lxE&size=320x200&markers=color:0x53C679%7Clabel:L%7C" + latLng;
        ImageView mapImg = (ImageView) viewHolder.grpLocationMapImg;
        DownloadImageTask downloadImageTask = new DownloadImageTask(mapImg);
        downloadImageTask.execute(url);


        if (isHome) {
            viewHolder.grpJoinBtn.setText(R.string.card_btn_leave);
            viewHolder.grpJoinBtn.setBackgroundColor(mContext.getResources().getColor(R.color.colorError, null));
            viewHolder.grpJoinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LeaveGroupDialogFragment leaveGroupDialog = new LeaveGroupDialogFragment();
                    FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    leaveGroupDialog.setStudyGroup(studyGroup);
                    leaveGroupDialog.setListener(StudyGroupAdapter.this);
                    leaveGroupDialog.show(fm, "Leave Group Fragment");
                }
            });

        } else {
            viewHolder.grpJoinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataSnapshot dsGroups = dataSnapshot.child("groups");
                            Iterable<DataSnapshot> dsGroupsChildren = dsGroups.getChildren();
                            for (DataSnapshot dsGroupsChild : dsGroupsChildren) {
                                StudyGroup grp = dsGroupsChild.getValue(StudyGroup.class);
                                String compareID = studyGroup.getGroupID();
                                if (grp.getGroupID().equals(compareID)) {
                                    Participant p = new Participant(uid, name, "comment", 3, false);
                                    grp.addParticipant(p);
                                    DatabaseReference drGroups = dsGroupsChild.getRef();
                                    drGroups.setValue(grp);
                                    Toast.makeText(mContext, "joined study group", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });
        }

        // TODO probably obsolete, delete if no longer needed
        viewHolder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(viewHolder.overflow);
            }
        });
    }

    private void hideDetails(MyViewHolder viewHolder) {
        viewHolder.grpDetails.setVisibility(View.GONE);
        viewHolder.grpDetailsImg.setVisibility(View.GONE);
        viewHolder.grpLocation.setVisibility(View.GONE);
        viewHolder.grpLocationDetails.setVisibility(View.GONE);
        viewHolder.grpLocationImg.setVisibility(View.GONE);
        viewHolder.grpLocationMapImg.setVisibility(View.GONE);
        viewHolder.divider1.setVisibility(View.GONE);
        viewHolder.grpParticipantsImg2.setVisibility(View.GONE);
        viewHolder.grpParticipantsDetails.setVisibility(View.GONE);
        viewHolder.divider2.setVisibility(View.GONE);
        viewHolder.grpJoinBtn.setVisibility(View.GONE);
    }

    private void setParticipantsDetails(MyViewHolder viewHolder, StudyGroup studyGroup) {
        String text = mContext.getString(R.string.card_txt_participants_details);
        ArrayList<Participant> participants = studyGroup.getParticipants();
        if (participants != null) {
            for (int i = 0; i < participants.size(); i++) {
                Participant p = participants.get(i);
                String name = p.getName();
                String comment = p.getComment();
                if (comment.equals("")) {
                    text += "\n\n" + name;
                } else {
                    text += "\n\n" + name + ":\n" + "\"" + comment + "\"";
                }
            }
        }
        viewHolder.grpParticipantsDetails.setText(text);
    }

    private void setStars(MyViewHolder viewHolder, StudyGroup studyGroup) {
        int maxKnowledge = 6;
        ArrayList<Participant> participants = studyGroup.getParticipants();
        double stars = 0;
        if (participants != null) {
            for (int i = 0; i < participants.size(); i++) {
                stars += participants.get(i).getKnowledge();
            }
            stars /= participants.size();
        }
        if (stars < 0.1 * maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            return;
        }
        if (stars < 0.2 * maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_half_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            return;
        }
        if (stars < 0.3 * maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            return;
        }
        if (stars < 0.4 * maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_half_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            return;
        }
        if (stars < 0.5 * maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            return;
        }
        if (stars < 0.6 * maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_half_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            return;
        }
        if (stars < 0.7 * maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            return;
        }
        if (stars < 0.8 * maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_half_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            return;
        }
        if (stars < 0.9 * maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            return;
        }
        if (stars < maxKnowledge) {
            viewHolder.star1.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_half_black_24dp);
        } else {
            viewHolder.star1.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star2.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star3.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star4.setImageResource(R.drawable.ic_star_black_24dp);
            viewHolder.star5.setImageResource(R.drawable.ic_star_black_24dp);
        }
    }

    // TODO can be deleted if menu is not used
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_group, popupMenu.getMenu());

        ViewParent viewParent = view.getParent();
        View parentView = (View) viewParent;

        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener(parentView));
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return studyGroups.size();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        final StudyGroup studyGroup = ((LeaveGroupDialogFragment) dialog).studyGroup;
        Toast.makeText(mContext, "positive click", Toast.LENGTH_SHORT).show();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dsGroups = dataSnapshot.child("groups");
                Iterable<DataSnapshot> dsGroupsChildren = dsGroups.getChildren();
                for (DataSnapshot dsGroupsChild : dsGroupsChildren) {
                    StudyGroup grp = dsGroupsChild.getValue(StudyGroup.class);
                    if (grp.getGroupID().equals(studyGroup.getGroupID())) {
                        DataSnapshot dsParticipants = dsGroupsChild.child("participants");
                        Iterable<DataSnapshot> dsParticipantsChildren = dsParticipants.getChildren();
                        for (DataSnapshot dsParticipantsChild : dsParticipantsChildren) {
                            Participant p = dsParticipantsChild.getValue(Participant.class);
                            if (p.getUid().equals(uid)) {
                                dsParticipantsChild.getRef().removeValue();
                                int participantCount = grp.getParticipantCount();
                                participantCount--;
                                dsGroupsChild.getRef().child("participantCount").setValue(participantCount);
                                Toast.makeText(mContext, "left study group", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(mContext, "negative click", Toast.LENGTH_SHORT).show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView grpName;
        public TextView grpParticipants;
        public TextView grpDate;
        public TextView grpTime;
        public TextView grpDetails;
        public TextView grpLocation;
        public TextView grpLocationDetails;
        public TextView grpParticipantsDetails;

        public ImageView grpDetailsImg;
        public ImageView grpLocationImg;
        public ImageView grpLocationMapImg;
        public ImageView grpParticipantsImg2;

        public View divider1;
        public View divider2;

        public ImageView star1;
        public ImageView star2;
        public ImageView star3;
        public ImageView star4;
        public ImageView star5;

        public Button grpJoinBtn;

        public ImageView overflow;

        public MyViewHolder(View view) {
            super(view);

            grpName = (TextView) view.findViewById(R.id.txt_card_grp_name);
            grpParticipants = (TextView) view.findViewById(R.id.txt_card_grp_participants);
            grpDate = (TextView) view.findViewById(R.id.txt_card_grp_date);
            grpTime = (TextView) view.findViewById(R.id.txt_card_grp_time);
            grpDetails = (TextView) view.findViewById(R.id.txt_card_grp_details);
            grpLocation = (TextView) view.findViewById(R.id.txt_card_grp_location);
            grpLocationDetails = (TextView) view.findViewById(R.id.txt_card_grp_location_details);
            grpParticipantsDetails = (TextView) view.findViewById(R.id.txt_card_grp_participants_details);

            divider1 = view.findViewById(R.id.divider_card_participants);
            divider2 = view.findViewById(R.id.divider_card_participants2);
            grpParticipantsImg2 = (ImageView) view.findViewById(R.id.img_card_grp_participants2);

            grpDetailsImg = (ImageView) view.findViewById(R.id.img_card_grp_details);
            grpLocationImg = (ImageView) view.findViewById(R.id.img_card_grp_location);
            grpLocationMapImg = (ImageView) view.findViewById(R.id.img_location_map);

            star1 = (ImageView) view.findViewById(R.id.img_card_grp_star1);
            star2 = (ImageView) view.findViewById(R.id.img_card_grp_star2);
            star3 = (ImageView) view.findViewById(R.id.img_card_grp_star3);
            star4 = (ImageView) view.findViewById(R.id.img_card_grp_star4);
            star5 = (ImageView) view.findViewById(R.id.img_card_grp_star5);

            grpJoinBtn = (Button) view.findViewById(R.id.btn_card_grp_join);

            overflow = (ImageView) view.findViewById(R.id.img_card_grp_menu);
        }
    }

    class CardItemListener implements CardView.OnClickListener {
        @Override
        public void onClick(View view) {
            int visibilityStatus = view.findViewById(R.id.txt_card_grp_details).getVisibility();
            if (visibilityStatus == View.GONE) {
                view.findViewById(R.id.txt_card_grp_details).setVisibility(View.VISIBLE);
                view.findViewById(R.id.img_card_grp_details).setVisibility(View.VISIBLE);
                view.findViewById(R.id.txt_card_grp_location).setVisibility(View.VISIBLE);
                view.findViewById(R.id.txt_card_grp_location_details).setVisibility(View.VISIBLE);
                view.findViewById(R.id.img_card_grp_location).setVisibility(View.VISIBLE);
                view.findViewById(R.id.img_location_map).setVisibility(View.VISIBLE);
                view.findViewById(R.id.divider_card_participants).setVisibility(View.VISIBLE);
                view.findViewById(R.id.img_card_grp_participants2).setVisibility(View.VISIBLE);
                view.findViewById(R.id.txt_card_grp_participants_details).setVisibility(View.VISIBLE);
                view.findViewById(R.id.divider_card_participants2).setVisibility(View.VISIBLE);
                view.findViewById(R.id.divider_card_participants2).setVisibility(View.VISIBLE);
                view.findViewById(R.id.btn_card_grp_join).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.txt_card_grp_details).setVisibility(View.GONE);
                view.findViewById(R.id.img_card_grp_details).setVisibility(View.GONE);
                view.findViewById(R.id.txt_card_grp_location).setVisibility(View.GONE);
                view.findViewById(R.id.txt_card_grp_location_details).setVisibility(View.GONE);
                view.findViewById(R.id.img_card_grp_location).setVisibility(View.GONE);
                view.findViewById(R.id.img_location_map).setVisibility(View.GONE);
                view.findViewById(R.id.divider_card_participants).setVisibility(View.GONE);
                view.findViewById(R.id.img_card_grp_participants2).setVisibility(View.GONE);
                view.findViewById(R.id.txt_card_grp_participants_details).setVisibility(View.GONE);
                view.findViewById(R.id.divider_card_participants2).setVisibility(View.GONE);
                view.findViewById(R.id.btn_card_grp_join).setVisibility(View.GONE);
            }
        }
    }

    // TODO can be deleted if menu is not used, if kept remove duplicate code...
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private View view;

        public MyMenuItemClickListener(View view) {
            this.view = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.action_show_details:
                    int visibilityStatus = view.findViewById(R.id.txt_card_grp_details).getVisibility();
                    if (visibilityStatus == View.GONE) {
                        view.findViewById(R.id.txt_card_grp_details).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.img_card_grp_details).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.txt_card_grp_location).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.txt_card_grp_location_details).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.img_card_grp_location).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.img_location_map).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.divider_card_participants).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.img_card_grp_participants2).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.txt_card_grp_participants_details).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.divider_card_participants2).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.divider_card_participants2).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.btn_card_grp_join).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.txt_card_grp_details).setVisibility(View.GONE);
                        view.findViewById(R.id.img_card_grp_details).setVisibility(View.GONE);
                        view.findViewById(R.id.txt_card_grp_location).setVisibility(View.GONE);
                        view.findViewById(R.id.txt_card_grp_location_details).setVisibility(View.GONE);
                        view.findViewById(R.id.img_card_grp_location).setVisibility(View.GONE);
                        view.findViewById(R.id.img_location_map).setVisibility(View.GONE);
                        view.findViewById(R.id.divider_card_participants).setVisibility(View.GONE);
                        view.findViewById(R.id.img_card_grp_participants2).setVisibility(View.GONE);
                        view.findViewById(R.id.txt_card_grp_participants_details).setVisibility(View.GONE);
                        view.findViewById(R.id.divider_card_participants2).setVisibility(View.GONE);
                        view.findViewById(R.id.btn_card_grp_join).setVisibility(View.GONE);
                    }
                    return true;

                case R.id.action_join_group:
                    Toast.makeText(mContext, "TODO: join group", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mapImg = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mapImg = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mapImg;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}
