package ru.alekssey7227.lifetime.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.adapters.GoalsRVAdapter;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.database.DBHelper;
import ru.alekssey7227.lifetime.fragments.GoalDialogFragment;

public class GoalActivity extends AppCompatActivity {

    private Toolbar gToolBar;
    private DBHelper dbHelper;
    private Goal goal;

    private TextView txtGAName;
    private TextView txtGATime;
    private TextView txtGAIteration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        gToolBar = findViewById(R.id.goal_toolbar);
        setSupportActionBar(gToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txtGAName = findViewById(R.id.txtGANameLabel);
        txtGATime = findViewById(R.id.txtGATime);
        txtGAIteration = findViewById(R.id.txtGAIteration);

        int id = getIntent().getIntExtra(GoalsRVAdapter.MAIN_ACTIVITY_EXTRA, 0);
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        goal = dbHelper.getById(database, id);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (goal != null) {
                txtGAName.setText(goal.getName().toUpperCase());
                txtGATime.setText(goal.getTime().getTimeInHoursStringFormatted());
                txtGAIteration.setText(goal.getIteration().getTimeInHoursStringFormatted());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.goal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Редактирование цели
        if (item.getItemId() == R.id.app_bar_edit_goal) {
            GoalDialogFragment.display(getSupportFragmentManager(), goal);
        } else if (item.getItemId() == R.id.app_bar_delete_goal) {
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.deleteGoal(db, goal);
            MainActivity.getInstance().rvUpdate();
            finish();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}