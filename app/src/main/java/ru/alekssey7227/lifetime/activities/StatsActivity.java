package ru.alekssey7227.lifetime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

        PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_home).withSelectable(false);
        PrimaryDrawerItem statistics = new PrimaryDrawerItem().withName("Statistics").withIcon(R.drawable.ic_bar_chart).withSelectable(false);

        SecondaryDrawerItem settings = new SecondaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_action_settings).withSelectable(false);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(gaToolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(home, statistics, new DividerDrawerItem(), settings)
                .withSelectedItem(-2)
                .build();


        SecondaryDrawerItem footer = new SecondaryDrawerItem().withName("Made by Aleksey Shulikov").withSelectable(false);
        result.addStickyFooterItem(footer);

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