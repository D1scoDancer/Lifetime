package ru.alekssey7227.lifetime.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.backend.Goal;


public class GoalsRVAdapter extends RecyclerView.Adapter<GoalsRVAdapter.ViewHolder> {

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
        holder.txtTime.setText(goals.get(position).getTimeInHours());  //TODO: fix

        holder.parent.setOnClickListener(v -> Toast.makeText(v.getContext(), goals.get(position).toString(), Toast.LENGTH_SHORT).show());
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
