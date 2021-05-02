package ru.alekssey7227.lifetime.adapters;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.activities.GoalActivity;
import ru.alekssey7227.lifetime.activities.MainActivity;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.backend.StatsUnit;
import ru.alekssey7227.lifetime.database.GoalDBHelper;
import ru.alekssey7227.lifetime.database.StatsDBHelper;


public class GoalsRVAdapter extends RecyclerView.Adapter<GoalsRVAdapter.ViewHolder> {

    public static final String MAIN_ACTIVITY_EXTRA = "id";

    private List<Goal> goals = new ArrayList<>();

    private MainActivity mainActivity;

    public GoalsRVAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_list_item, parent, false);
        return new ViewHolder(view);
    }

    // there you can also set onClickListeners for UI elements
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(goals.get(position).getName());
        holder.txtTime.setText(goals.get(position).getTime().getTimeInHoursStringFormatted());

        TypedArray icons = mainActivity.getResources().obtainTypedArray(R.array.goal_icons);
        holder.ivIcon.setImageDrawable(icons.getDrawable(goals.get(position).getImage()));

        holder.parent.setOnClickListener(v -> { // call GoalActivity
            int id = goals.get(position).getId();
            Intent intent = new Intent(mainActivity, GoalActivity.class);
            intent.putExtra(MAIN_ACTIVITY_EXTRA, id);
            v.getContext().startActivity(intent);
        });

        holder.parent.setOnLongClickListener(v -> { // increment  TODO: maybe change for double tap and this one will go for other func
            Goal goal = goals.get(position);
            goal.increment();

            handleGoalDB(goal);

            mainActivity.rvUpdate();

            handleStatsDB(goal);

            return true;
        });
    }

    private void handleGoalDB(Goal goal) {
        GoalDBHelper dbHelper = new GoalDBHelper(mainActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.updateGoal(db, goal);
        notifyDataSetChanged();
    }

    private void handleStatsDB(Goal goal) {
        StatsDBHelper statsDBHelper = new StatsDBHelper(mainActivity);

        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int year = Calendar.getInstance().get(Calendar.YEAR);


        StatsUnit unit = statsDBHelper.get(goal.getId(), day, month, year);
        SQLiteDatabase db = statsDBHelper.getWritableDatabase();

        if(unit != null){
            unit.addTime(goal.getIteration().getTimeInMinutes());
            statsDBHelper.updateStatsUnit(db, unit);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(StatsDBHelper.KEY_GOAL_ID, goal.getId());
            cv.put(StatsDBHelper.KEY_DAY, day);
            cv.put(StatsDBHelper.KEY_MONTH, month);
            cv.put(StatsDBHelper.KEY_YEAR, year);
            cv.put(StatsDBHelper.KEY_ESTIMATED_TIME, goal.getIteration().getTimeInMinutes());
            db.insert(StatsDBHelper.TABLE_STATS, null, cv);
        }
        notifyDataSetChanged();
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
        private final CardView parent;
        private ImageView ivIcon;
        private final TextView txtName;
        private final TextView txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            txtName = itemView.findViewById(R.id.txtName);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
