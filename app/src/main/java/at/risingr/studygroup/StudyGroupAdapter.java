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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StudyGroupAdapter extends RecyclerView.Adapter<StudyGroupAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<StudyGroup> studyGroups;

    public StudyGroupAdapter(Context mContext, ArrayList<StudyGroup> studyGroups) {
        this.mContext = mContext;
        this.studyGroups = studyGroups;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_group, parent, false);
        itemView.setOnClickListener(new CardItemListener());
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, int position) {
        StudyGroup studyGroup = studyGroups.get(position);
        viewHolder.grpName.setText(studyGroup.getGroupName());
        viewHolder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(viewHolder.overflow);
            }
        });
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
        public ImageView overflow;

        public MyViewHolder(View view) {
            super(view);
            grpName = (TextView) view.findViewById(R.id.txt_card_grp_name);
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
                view.findViewById(R.id.img_card_grp_location).setVisibility(View.VISIBLE);
                view.findViewById(R.id.btn_card_grp_join).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.txt_card_grp_details).setVisibility(View.GONE);
                view.findViewById(R.id.img_card_grp_details).setVisibility(View.GONE);
                view.findViewById(R.id.txt_card_grp_location).setVisibility(View.GONE);
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
