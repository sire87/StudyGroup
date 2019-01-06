package at.risingr.studygroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener());
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
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {


        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_show_details:
                    Toast.makeText(mContext, "show details TODO", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_join_group:
                    Toast.makeText(mContext, "join group TODO", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    }
}
