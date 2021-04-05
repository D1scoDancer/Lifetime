package ru.alekssey7227.lifetime.activities;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;

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
    private ImageView ivGAIcon;

    private Chronometer chronometer;
    private Button btnStart;
    private Button btnPause;
    private Button btnStop;

    private SharedPreferences preferences;
    private long startTime = 0;
    private long stopTime = 0;
    private boolean isRunning;
    private boolean isPaused;

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
        ivGAIcon = findViewById(R.id.ivGAIcon);

        chronometer = findViewById(R.id.chronometer);
        // Время, отображаемое хронометром
        chronometer.setOnChronometerTickListener(chronometer -> {
            long time = Calendar.getInstance().getTime().getTime() - startTime;
            long h = time / 3600000;
            long m = (time - h * 3600000) / 60000;
            long s = (time - h * 3600000 - m * 60000) / 1000;
            chronometer.setText(String.format(Locale.ENGLISH, "%02d:%02d:%02d", h, m, s));
        });


        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this::onStartButton);
        btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(this::onPauseButton);
        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this::onStopButton);

        int id = getIntent().getIntExtra(GoalsRVAdapter.MAIN_ACTIVITY_EXTRA, 0);
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        goal = dbHelper.getById(database, id);
    }

    @Override
    protected void onStart() {
        super.onStart();

        preferences = getPreferences(MODE_PRIVATE);
        startTime = preferences.getLong("startTime", 0);
        stopTime = preferences.getLong("stopTime", 0);
        isRunning = preferences.getBoolean("isRunning", false);
        isPaused = preferences.getBoolean("isPaused", false);

        if (isRunning) {
            chronometer.start();
            btnStart.setEnabled(false);
            btnPause.setEnabled(true);
            btnStop.setEnabled(true);
        } else if (isPaused) {
            btnStart.setEnabled(true);
            btnPause.setEnabled(false);
            btnStop.setEnabled(true);

            long time = stopTime - startTime;
            long h = time / 3600000;
            long m = (time - h * 3600000) / 60000;
            long s = (time - h * 3600000 - m * 60000) / 1000;
            chronometer.setText(String.format(Locale.ENGLISH, "%02d:%02d:%02d", h, m, s));
        } else {
            btnStart.setEnabled(true);
            btnPause.setEnabled(false);
            btnStop.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("startTime", startTime);
        editor.putLong("stopTime", stopTime);
        editor.putBoolean("isRunning", isRunning);
        editor.putBoolean("isPaused", isPaused);
        editor.apply();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (goal != null) {
                txtGAName.setText(goal.getName().toUpperCase());
                txtGATime.setText(goal.getTime().getTimeInHoursStringFormatted());
                txtGAIteration.setText(goal.getIteration().getTimeInHoursStringFormatted());
                TypedArray icons = getResources().obtainTypedArray(R.array.goal_icons);
                ivGAIcon.setImageDrawable(icons.getDrawable(goal.getImage()));
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

    public void onStartButton(View v) {
        startTime = Calendar.getInstance().getTime().getTime() - (stopTime - startTime);
        isRunning = true;
        isPaused = false;
        chronometer.start();

        btnStart.setEnabled(false);
        btnPause.setEnabled(true);
        btnStop.setEnabled(true);
    }

    public void onPauseButton(View v) {
        stopTime = Calendar.getInstance().getTime().getTime();
        isRunning = false;
        isPaused = true;
        chronometer.stop();

        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnStop.setEnabled(true);
    }

    public void onStopButton(View v) {
        stopTime = 0;
        startTime = 0;
        isRunning = false;
        isPaused = false;
        chronometer.stop();

        chronometer.setText(String.format(Locale.ENGLISH, "%02d:%02d:%02d", 0, 0, 0));

        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
    }
}