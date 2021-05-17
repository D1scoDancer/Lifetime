package ru.alekssey7227.lifetime.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.adapters.StatsSectionsPagerAdapter;

public class StatsActivity extends AppCompatActivity {
    private Toolbar gaToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        StatsSectionsPagerAdapter statsSectionsPagerAdapter = new StatsSectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(statsSectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        SharedPreferences preferences = getSharedPreferences("night_mode", MODE_PRIVATE);
        int nightMode = preferences.getInt("mode", -1);
        if (nightMode != -1 && nightMode != AppCompatDelegate.MODE_NIGHT_NO) {
            tabs.setBackgroundColor(Color.parseColor("#121212"));
            tabs.setTabTextColors(Color.WHITE, Color.GRAY);
        }

        gaToolbar = findViewById(R.id.GA_toolbar);
        setSupportActionBar(gaToolbar);

        createNavigationDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.stats_menu, menu);
        return true;
    }

    private void createNavigationDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ga_2_lotus_position)
                .build();

        PrimaryDrawerItem home = new PrimaryDrawerItem().withName(R.string.nd_home).withIcon(R.drawable.ic_home).withSelectable(false);
        PrimaryDrawerItem statistics = new PrimaryDrawerItem().withName(R.string.nd_stats).withIcon(R.drawable.ic_bar_chart).withSelectable(false);

        SecondaryDrawerItem settings = new SecondaryDrawerItem().withName(R.string.nd_settings).withIcon(R.drawable.ic_action_settings).withSelectable(false);

        DrawerBuilder builder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(gaToolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(home, statistics, new DividerDrawerItem(), settings)
                .withSelectedItem(-2);

        SharedPreferences preferences = getSharedPreferences("night_mode", MODE_PRIVATE);
        int nightMode = preferences.getInt("mode", -1);
        if (nightMode != -1 && nightMode != AppCompatDelegate.MODE_NIGHT_NO) {
            builder.withSliderBackgroundColor(Color.parseColor("#121212"));

            Drawable homeWhite = ContextCompat.getDrawable(this, R.drawable.ic_home);
            Drawable statsWhite = ContextCompat.getDrawable(this, R.drawable.ic_bar_chart);
            Drawable settingsWhite = ContextCompat.getDrawable(this, R.drawable.ic_action_settings);
            if (homeWhite != null) {
                homeWhite.setTint(Color.WHITE);
            }
            if (statsWhite != null) {
                statsWhite.setTint(Color.WHITE);
            }
            if (settingsWhite != null) {
                settingsWhite.setTint(Color.WHITE);
            }

            home.withTextColor(Color.WHITE).withIconColor(Color.WHITE).withIcon(homeWhite);
            statistics.withTextColor(Color.WHITE).withIconColor(Color.WHITE).withIcon(statsWhite);
            settings.withTextColor(Color.WHITE).withIconColor(Color.WHITE).withIcon(settingsWhite);
        }

        Drawer result = builder.build();

        result.setOnDrawerItemClickListener((view, position, drawerItem) -> {
            switch (position) {
                case 1:
                    this.finish();
                    break;
                case 2:
                    result.closeDrawer();
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
}