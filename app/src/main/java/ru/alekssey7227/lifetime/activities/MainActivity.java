package ru.alekssey7227.lifetime.activities;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.List;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.adapters.GoalsRVAdapter;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.database.DBHelper;
import ru.alekssey7227.lifetime.fragments.GoalDialogFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private RecyclerView goalsRV;
    private DBHelper dbHelper;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        mToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolBar);

        goalsRV = findViewById(R.id.goalsRV);

        dbHelper = new DBHelper(this);
        rvUpdate();

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ga_2_lotus_position)
                .build();

        PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_home).withSelectable(false);
        PrimaryDrawerItem statistics = new PrimaryDrawerItem().withName("Statistics").withIcon(R.drawable.ic_bar_chart).withSelectable(false);

        SecondaryDrawerItem settings = new SecondaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_action_settings).withSelectable(false);
//        SecondaryDrawerItem nightMode  TODO: если получится, то сделать свитчер для ночного режима

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolBar)
                .withAccountHeader(headerResult)
                .addDrawerItems(home, statistics, new DividerDrawerItem(), settings)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                    return true;
                })
                .withSelectedItem(-2)
                .build();


        SecondaryDrawerItem footer = new SecondaryDrawerItem().withName("Made by Aleksey Shulikov").withSelectable(false);
        result.addStickyFooterItem(footer);
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
}