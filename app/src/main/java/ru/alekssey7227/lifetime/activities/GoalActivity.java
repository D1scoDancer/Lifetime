package ru.alekssey7227.lifetime.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.adapters.GoalsRVAdapter;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.database.DBHelper;

public class GoalActivity extends AppCompatActivity {

    private Toolbar gToolBar;
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.goal_menu, menu);
        return true;
    }
}