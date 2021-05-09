package ru.alekssey7227.lifetime.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.util.Calendar;
import java.util.List;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.adapters.GoalsRVAdapter;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.database.GoalDBHelper;
import ru.alekssey7227.lifetime.fragments.GoalDialogFragment;
import ru.alekssey7227.lifetime.others.MyAlarm;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private RecyclerView goalsRV;
    private GoalDBHelper dbHelper;
    private Drawer result;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        SharedPreferences preferences = getSharedPreferences("night_mode", MODE_PRIVATE);
        int nightMode = preferences.getInt("mode", -1);
        if(nightMode != -1){
            AppCompatDelegate.setDefaultNightMode(nightMode);
        }

        mToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolBar);

        goalsRV = findViewById(R.id.goalsRV);

        dbHelper = new GoalDBHelper(this);
        rvUpdate();

        createNavigationDrawer();

        setAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Добавление новой цели
        if (item.getItemId() == R.id.app_bar_add_goal) {
            GoalDialogFragment.display(getSupportFragmentManager());
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void rvUpdate() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        List<Goal> goals = dbHelper.readAllGoals(database);

        GoalsRVAdapter adapter = new GoalsRVAdapter(this);
        adapter.setGoals(goals);
        goalsRV.setAdapter(adapter);
        goalsRV.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createNavigationDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ga_2_lotus_position)
                .build();

        PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_home).withSelectable(false);
        PrimaryDrawerItem statistics = new PrimaryDrawerItem().withName("Statistics").withIcon(R.drawable.ic_bar_chart).withSelectable(false);

        SecondaryDrawerItem settings = new SecondaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_action_settings).withSelectable(false);

        DrawerBuilder builder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolBar)
                .withAccountHeader(headerResult)
                .addDrawerItems(home, statistics, new DividerDrawerItem(), settings)
                .withSelectedItem(-2);

        SharedPreferences preferences = getSharedPreferences("night_mode", MODE_PRIVATE);
        int nightMode = preferences.getInt("mode", -1);
        if(nightMode != -1 && nightMode != AppCompatDelegate.MODE_NIGHT_NO){
            builder.withSliderBackgroundColor(Color.parseColor("#121212"));

            Drawable homeWhite = ContextCompat.getDrawable(this, R.drawable.ic_home);
            Drawable statsWhite = ContextCompat.getDrawable(this, R.drawable.ic_bar_chart);
            Drawable settingsWhite = ContextCompat.getDrawable(this, R.drawable.ic_action_settings);
            if(homeWhite!= null){
                homeWhite.setTint(Color.WHITE);
            }
            if(statsWhite!= null){
                statsWhite.setTint(Color.WHITE);
            }
            if(settingsWhite!= null){
                settingsWhite.setTint(Color.WHITE);
            }

            home.withTextColor(Color.WHITE).withIconColor(Color.WHITE).withIcon(homeWhite);
            statistics.withTextColor(Color.WHITE).withIconColor(Color.WHITE).withIcon(statsWhite);
            settings.withTextColor(Color.WHITE).withIconColor(Color.WHITE).withIcon(settingsWhite);
        }

        result = builder.build();

        result.setOnDrawerItemClickListener((view, position, drawerItem) -> {
            switch (position) {
                case 1:
                    result.closeDrawer();
                    break;
                case 2:
                    startActivity(new Intent(this, StatsActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(this, SettingsActivity.class));
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        result.closeDrawer();
        rvUpdate();
    }

    private void setAlarm() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);

        long ms = calendar.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, MyAlarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC, ms, AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}