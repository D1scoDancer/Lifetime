package ru.alekssey7227.lifetime.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.activities.GoalActivity;
import ru.alekssey7227.lifetime.backend.Goal;


public class GoalsRVAdapter extends RecyclerView.Adapter<GoalsRVAdapter.ViewHolder> {

    public static final String MAIN_ACTIVITY_EXTRA = "id";

    private List<Goal> goals = new ArrayList<>();

    // private Context context;
    public GoalsRVAdapter() {
        // get context here if needed
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    // there you can also set onClickListeners for UI elements
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(goals.get(position).getName());
        holder.txtTime.setText(goals.get(position).getTime().getTimeInHoursString());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // call GoalActivity
                int id = goals.get(position).getId();
                Intent intent = new Intent(v.getContext(), GoalActivity.class);
                intent.putExtra(MAIN_ACTIVITY_EXTRA, id);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView parent;
        private ImageView ivIcon;
        private TextView txtName, txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            txtName = itemView.findViewById(R.id.txtName);
            txtTime = itemView.findViewById(R.id.txtTime);

        }
    }
}
