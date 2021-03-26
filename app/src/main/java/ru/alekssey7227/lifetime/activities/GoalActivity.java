package ru.alekssey7227.lifetime.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.adapters.GoalsRVAdapter;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.database.DBHelper;

public class GoalActivity extends AppCompatActivity {

    private Toolbar gToolBar;
    private TextView txtGoalName;
    private TextView txtGoalTime;
    private TextView txtGoalIteration;

    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    private ImageButton btn_startChronometer;
    private ImageButton btn_pauseChronometer;
    private ImageButton btn_stopChronometer;

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
            txtGoalIteration.setText(goal.getIteration().getTimeInHoursString());
        }

        chronometer = findViewById(R.id.chronometer);
        chronometer.setOnChronometerTickListener(chronometer -> {
            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
            int h   = (int)(time /3600000);
            int m = (int)(time - h*3600000)/60000;
            int s= (int)(time - h*3600000- m*60000)/1000 ;
            String hh = h < 10 ? "0"+h: h+"";
            String mm = m < 10 ? "0"+m: m+"";
            String ss = s < 10 ? "0"+s: s+"";
            chronometer.setText(String.format(Locale.ENGLISH, "%02d:%02d:%02d", h, m, s ));
        });

        btn_startChronometer = findViewById(R.id.btn_startChronometer);
        btn_startChronometer.setOnClickListener(this::startChronometer);

        btn_pauseChronometer = findViewById(R.id.btn_pauseChronometer);
        btn_pauseChronometer.setOnClickListener(this::pauseChronometer);

        btn_stopChronometer = findViewById(R.id.btn_stopChronometer);
        btn_stopChronometer.setOnClickListener(this::stopChronometer);
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.goal_menu, menu);
        return true;
    }

    public void startChronometer(View v) {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
            btn_startChronometer.setVisibility(View.GONE);
            btn_pauseChronometer.setVisibility(View.VISIBLE);
        }
    }

    public void pauseChronometer(View v) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
            btn_startChronometer.setVisibility(View.VISIBLE);
            btn_pauseChronometer.setVisibility(View.GONE);
        }
    }

    public void stopChronometer(View v) {
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        if (running) {
            running = false;
            btn_startChronometer.setVisibility(View.VISIBLE);
            btn_pauseChronometer.setVisibility(View.GONE);
        }
    }
}