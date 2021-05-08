package ru.alekssey7227.lifetime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import ru.alekssey7227.lifetime.R;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar sToolBar;
    private Spinner spinnerLanguage;
    private SwitchCompat switchDarkMode;
    private Button btnExport;
    private Button btnImport;
    private final String[] data = new String[]{"English", "Russian"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sToolBar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(sToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        btnExport = findViewById(R.id.btnExport);
        btnImport = findViewById(R.id.btnImport);

        int nightMode = getSharedPreferences("night_mode", MODE_PRIVATE).getInt("mode", -1);
        switchDarkMode.setChecked(nightMode == AppCompatDelegate.MODE_NIGHT_YES);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences preferences = getSharedPreferences("night_mode", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (isChecked && preferences.getInt("mode", -1) != AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putInt("mode", AppCompatDelegate.MODE_NIGHT_YES);
            } else if (preferences.getInt("mode", -1) != AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putInt("mode", AppCompatDelegate.MODE_NIGHT_NO);
            }
            editor.apply();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}