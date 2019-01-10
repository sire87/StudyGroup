package at.risingr.studygroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

public class StudyGroupAdapter extends RecyclerView.Adapter<StudyGroupAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<StudyGroup> studyGroups;
    private boolean isHome;

    public StudyGroupAdapter(Context mContext, ArrayList<StudyGroup> studyGroups, boolean isHome) {
        this.mContext = mContext;
        this.studyGroups = studyGroups;
        this.isHome = isHome;
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
        StudyGroup studyGroup = studyGroups.get(position);
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

        setStars(viewHolder, studyGroup);

        viewHolder.grpJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "TODO: join functionality", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(viewHolder.overflow);
            }
        });
    }

    private void setStars(MyViewHolder viewHolder, StudyGroup studyGroup) {
        int maxKnowledge = 6;
        ArrayList<Participant> participants = studyGroup.getParticipants();
        double stars = 0;
        for (int i = 0; i < participants.size(); i++) {
            stars += participants.get(i).getKnowledge();
        }
        stars /= participants.size();
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView grpName;
        public TextView grpParticipants;
        public TextView grpDate;
        public TextView grpTime;
        public TextView grpDetails;
        public TextView grpLocation;
        public TextView grpLocationDetails;

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
                if (!isHome) view.findViewById(R.id.btn_card_grp_join).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.txt_card_grp_details).setVisibility(View.GONE);
                view.findViewById(R.id.img_card_grp_details).setVisibility(View.GONE);
                view.findViewById(R.id.txt_card_grp_location).setVisibility(View.GONE);
                view.findViewById(R.id.txt_card_grp_location_details).setVisibility(View.GONE);
                view.findViewById(R.id.img_card_grp_location).setVisibility(View.GONE);
                view.findViewById(R.id.btn_card_grp_join).setVisibility(View.GONE);
            }
        }
    }

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
                        view.findViewById(R.id.img_card_grp_location).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.btn_card_grp_join).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.txt_card_grp_details).setVisibility(View.GONE);
                        view.findViewById(R.id.img_card_grp_details).setVisibility(View.GONE);
                        view.findViewById(R.id.txt_card_grp_location).setVisibility(View.GONE);
                        view.findViewById(R.id.img_card_grp_location).setVisibility(View.GONE);
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
}
