package ru.alekssey7227.lifetime.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.adapters.GoalsRVAdapter;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.database.DBHelper;

public class GoalActivity extends AppCompatActivity {

    private Toolbar gToolBar;
    private TextView txtGoalName;
    private TextView txtGoalTime;
    private TextView txtGoalIteration;

    private DBHelper dbHelper;
    private Goal goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        int id = getIntent().getIntExtra(GoalsRVAdapter.MAIN_ACTIVITY_EXTRA, 0);
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        goal = dbHelper.getById(database, id);

        gToolBar = findViewById(R.id.goal_toolbar);
        setSupportActionBar(gToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        txtGoalName = findViewById(R.id.txtGoalName);
        txtGoalTime = findViewById(R.id.txtGoalTime);
        txtGoalIteration = findViewById(R.id.txtGoalIteration);

        if (goal != null) {
            txtGoalName.setText(goal.getName());
            txtGoalTime.setText(goal.getTime().getTimeInHoursString());
            txtGoalIteration.setText(Long.toString(goal.getIteration())); // TODO: fix
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.goal_menu, menu);
        return true;
    }
}