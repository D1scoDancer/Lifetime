package ru.alekssey7227.lifetime.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.adapters.GoalsRVAdapter;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.backend.StatsUnit;
import ru.alekssey7227.lifetime.database.GoalDBHelper;
import ru.alekssey7227.lifetime.database.StatsDBHelper;
import ru.alekssey7227.lifetime.fragments.GoalDialogFragment;

public class GoalActivity extends AppCompatActivity {

    private Toolbar gToolBar;
    private GoalDBHelper dbHelper;
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
    private int goalId;

    private NotificationManager nm;
    private static final int NOTIFICATION_ID = 123;
    private static final String NOTIFICATION_CHANNEL_ID = "myChannelId";

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
        dbHelper = new GoalDBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        goal = dbHelper.getById(database, id);

        createBarChart();

        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        preferences = getPreferences(MODE_PRIVATE);
        startTime = preferences.getLong("startTime", 0);
        stopTime = preferences.getLong("stopTime", 0);
        isRunning = preferences.getBoolean("isRunning", false);
        isPaused = preferences.getBoolean("isPaused", false);
        goalId = preferences.getInt("goalId", -1);

        if (goalId != -1) {
            if (goalId != goal.getId()) {
                btnStart.setEnabled(false);
                btnPause.setEnabled(false);
                btnStop.setEnabled(false);
                return;
            }
        }

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

        if (!isPaused && !isRunning) {
            editor.putInt("goalId", -1);
        } else if (goalId == goal.getId() || goalId == -1) {
            editor.putInt("goalId", goal.getId());
        }
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
        }  // Удаление цели
        else if (item.getItemId() == R.id.app_bar_delete_goal) {
            GoalDBHelper dbHelper = new GoalDBHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.deleteGoal(db, goal);
            MainActivity.getInstance().rvUpdate();

            stopTime = 0;
            startTime = 0;
            isRunning = false;
            isPaused = false;
            chronometer.stop();

            StatsDBHelper statsDBHelper = new StatsDBHelper(this);
            SQLiteDatabase db2 = statsDBHelper.getWritableDatabase();
            statsDBHelper.deleteStatsUnits(db2, goal.getId());

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

        showNotification();
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
        long elapsedTime;
        if (isPaused) {
            elapsedTime = stopTime - startTime;
        } else {
            elapsedTime = Calendar.getInstance().getTime().getTime() - startTime;
        }

        stopTime = 0;
        startTime = 0;
        isRunning = false;
        isPaused = false;
        chronometer.stop();

        chronometer.setText(String.format(Locale.ENGLISH, "%02d:%02d:%02d", 0, 0, 0));

        long m = (elapsedTime) / 60000;
        goal.getTime().setTimeInMinutes(goal.getTime().getTimeInMinutes() + m); // TODO: maybe метод внутри Time

        GoalDBHelper dbHelper = new GoalDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.updateGoal(db, goal);
        txtGATime.setText(goal.getTime().getTimeInHoursStringFormatted());
        
        handleStatsDB(goal, m);
        createBarChart();

        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);

        stopNotification();
    }

    private void handleStatsDB(Goal goal, long m) {
        StatsDBHelper statsDBHelper = new StatsDBHelper(this);

        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int year = Calendar.getInstance().get(Calendar.YEAR);


        StatsUnit unit = statsDBHelper.get(goal.getId(), day, month, year);
        SQLiteDatabase db = statsDBHelper.getWritableDatabase();

        if (unit != null) {
            unit.addTime(m);
            statsDBHelper.updateStatsUnit(db, unit);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(StatsDBHelper.KEY_GOAL_ID, goal.getId());
            cv.put(StatsDBHelper.KEY_DAY, day);
            cv.put(StatsDBHelper.KEY_MONTH, month);
            cv.put(StatsDBHelper.KEY_YEAR, year);
            cv.put(StatsDBHelper.KEY_ESTIMATED_TIME, m);
            db.insert(StatsDBHelper.TABLE_STATS, null, cv);
        }
    }

    private void createBarChart() {
        BarChart barChart = findViewById(R.id.gaBarChart);

        StatsDBHelper statsDBHelper = new StatsDBHelper(this);
        List<StatsUnit> units = statsDBHelper.get(goal.getId());
        ArrayList<BarEntry> hoursPerDay = new ArrayList<>();

        for (int i = 0; i < units.size(); i++) {
            hoursPerDay.add(new BarEntry(i, (float) units.get(i).getEstimatedTime().getTimeInHours()));
        }

        BarDataSet barDataSet = new BarDataSet(hoursPerDay, goal.getName());
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long v = Math.round(value * 100.0);

                if (v % 100 == 0) {
                    return String.format(Locale.ENGLISH, "%d", (int) (v / 100.0));
                } else if (v % 10 == 0) {
                    return String.format(Locale.ENGLISH, "%.1f", v / 100.0);
                } else {
                    return String.format(Locale.ENGLISH, "%.2f", v / 100.0);
                }
            }
        });

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();

        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int) value;
                StatsUnit unit = units.get(i);
                String day = String.format(Locale.ENGLISH, "%02d", unit.getDay());
                String month = String.format(Locale.ENGLISH, "%02d", unit.getMonth());
                return day + "." + month;
            }
        });

        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setAxisMinimum(0f);
        barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long v = Math.round(value * 100.0);

                if (v % 100 == 0) {
                    return String.format(Locale.ENGLISH, "%d", (int) (v / 100.0));
                } else {
                    return String.format(Locale.ENGLISH, "%.1f", v / 100.0);
                }
            }
        });
        barChart.getAxisRight().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long v = Math.round(value * 100.0);

                if (v % 100 == 0) {
                    return String.format(Locale.ENGLISH, "%d", (int) (v / 100.0));
                } else {
                    return String.format(Locale.ENGLISH, "%.1f", v / 100.0);
                }
            }
        });

        barChart.getLegend().setEnabled(false);

        barChart.setDrawValueAboveBar(false);

        int nightMode = getSharedPreferences("night_mode", MODE_PRIVATE).getInt("mode", -1);
        if(nightMode == AppCompatDelegate.MODE_NIGHT_YES){
            barChart.getAxisRight().setTextColor(Color.WHITE);
            barChart.getAxisLeft().setTextColor(Color.WHITE);
            barChart.getXAxis().setTextColor(Color.WHITE);
            barDataSet.setValueTextColor(Color.WHITE);
        }

        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    private void showNotification() {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        Intent intent = new Intent(getApplicationContext(), GoalActivity.class);
        intent.putExtra(GoalsRVAdapter.MAIN_ACTIVITY_EXTRA, goal.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        TypedArray icons = getApplicationContext().getResources().obtainTypedArray(R.array.goal_icons);
        int icon = icons.getResourceId(goal.getImage(), 0);

        builder
                .setContentIntent(pendingIntent)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), icon))
                .setTicker("Timer started")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setContentTitle(goal.getName())
                .setContentText("Timer is running")
                .setProgress(100, 20, true)
        ;

        Notification notification = builder.build();
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, "My notification channel",
                    NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }

        nm.notify(NOTIFICATION_ID, notification);
    }

    private void stopNotification() {
        nm.cancel(NOTIFICATION_ID);
    }
}