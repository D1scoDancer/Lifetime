package ru.alekssey7227.lifetime.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.backend.StatsUnit;
import ru.alekssey7227.lifetime.database.GoalDBHelper;
import ru.alekssey7227.lifetime.database.StatsDBHelper;
import ru.alekssey7227.lifetime.others.LocaleHelper;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar sToolBar;
    private Spinner spinnerLanguage;
    private SwitchCompat switchDarkMode;
    private Button btnExport;
    private Button btnImport;

    private String[] data;

    private static final int STORAGE_REQUEST_CODE_EXPORT = 1;
    private static final int STORAGE_REQUEST_CODE_IMPORT = 2;
    private String[] storagePermissions;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle(R.string.title_settings_activity);

        data = new String[]{getString(R.string.language_english), getString(R.string.language_russian)};

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

        // BACKUP and RESTORE
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        btnImport.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                importCSV();
            } else {
                requestStoragePermissionImport();
            }
        });

        btnExport.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                exportCSV();
            } else {
                requestStoragePermissionExport();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("language", MODE_PRIVATE);
        String lang = sharedPreferences.getString("lang", "en");
        if(lang.equals("ru")){
            spinnerLanguage.setSelection(1);
        }

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences("language", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String lang = "";
                if (position == 0) {
                    lang = "en";
                } else {
                    lang = "ru";
                }
                if(sharedPreferences.getString("lang", "def").equals(lang)){
                    return;
                }
                editor.putString("lang", lang);
                editor.apply();
                LocaleHelper.setLocale(SettingsActivity.this, lang);

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
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

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermissionImport() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_IMPORT);
    }

    private void requestStoragePermissionExport() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_EXPORT);
    }

    private void importCSV() {
        String filePath1 = Environment.getExternalStorageDirectory() + "/" + "SQLiteBackup" + "/" + "backup1.csv";
        String filePath2 = Environment.getExternalStorageDirectory() + "/" + "SQLiteBackup" + "/" + "backup2.csv";

        File csvFile1 = new File(filePath1);
        File csvFile2 = new File(filePath2);

        if (csvFile1.exists() && csvFile2.exists()) {

            GoalDBHelper dbHelper = new GoalDBHelper(this);
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
            db1.execSQL("delete from " + GoalDBHelper.TABLE_GOALS);

            StatsDBHelper statsDBHelper = new StatsDBHelper(this);
            SQLiteDatabase db2 = statsDBHelper.getWritableDatabase();
            db2.execSQL("delete from " + StatsDBHelper.TABLE_STATS);


            try {
                CSVReader csvReader1 = new CSVReader(new FileReader(csvFile1.getAbsolutePath()));
                String[] nextLine1;
                while ((nextLine1 = csvReader1.readNext()) != null) {
                    String id = nextLine1[0];
                    String name = nextLine1[1];
                    String time = nextLine1[2];
                    String iteration = nextLine1[3];
                    String image = nextLine1[4];

                    ContentValues cv = new ContentValues();
                    cv.put(GoalDBHelper.KEY_ID, Integer.parseInt(id));
                    cv.put(GoalDBHelper.KEY_NAME, name);
                    cv.put(GoalDBHelper.KEY_TIME, Integer.parseInt(time));
                    cv.put(GoalDBHelper.KEY_ITERATION, Integer.parseInt(iteration));
                    cv.put(GoalDBHelper.KEY_IMAGE, Integer.parseInt(image));

                    db1.insert(GoalDBHelper.TABLE_GOALS, null, cv);
                }

                CSVReader csvReader2 = new CSVReader(new FileReader(csvFile2.getAbsolutePath()));
                String[] nextLine2;
                while ((nextLine2 = csvReader2.readNext()) != null) {
                    String id = nextLine2[0];
                    String goal_id = nextLine2[1];
                    String day = nextLine2[2];
                    String month = nextLine2[3];
                    String year = nextLine2[4];
                    String et = nextLine2[5];

                    ContentValues cv = new ContentValues();
                    cv.put(StatsDBHelper.KEY_ID, Integer.parseInt(id));
                    cv.put(StatsDBHelper.KEY_GOAL_ID, Integer.parseInt(goal_id));
                    cv.put(StatsDBHelper.KEY_DAY, Integer.parseInt(day));
                    cv.put(StatsDBHelper.KEY_MONTH, Integer.parseInt(month));
                    cv.put(StatsDBHelper.KEY_YEAR, Integer.parseInt(year));
                    cv.put(StatsDBHelper.KEY_ESTIMATED_TIME, Long.parseLong(et));

                    db2.insert(StatsDBHelper.TABLE_STATS, null, cv);
                }
                Toast.makeText(this, getString(R.string.settings_success_import), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.settings_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.settings_error_backup), Toast.LENGTH_SHORT).show();
        }
    }

    private void exportCSV() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + "SQLiteBackup");

        boolean isFolderCreated = false;
        if (!folder.exists()) {
            isFolderCreated = folder.mkdir();
        }

        Log.d("CSV_TAG", "exportCSV: " + isFolderCreated);

        String csvFileName1 = "backup1.csv";
        String csvFileName2 = "backup2.csv";

        String filePath1 = folder.toString() + "/" + csvFileName1;
        String filePath2 = folder.toString() + "/" + csvFileName2;

        GoalDBHelper dbHelper = new GoalDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Goal> records1 = dbHelper.readAllGoals(db);

        StatsDBHelper statsDBHelper = new StatsDBHelper(this);
        SQLiteDatabase db2 = statsDBHelper.getWritableDatabase();
        List<StatsUnit> records2 = statsDBHelper.readAllUnits(db2);

        try {
            FileWriter fw1 = new FileWriter(filePath1);
            for (int i = 0; i < records1.size(); i++) {
                fw1.append("" + records1.get(i).getId());
                fw1.append(",");
                fw1.append("" + records1.get(i).getName());
                fw1.append(",");
                fw1.append("" + records1.get(i).getTime().getTimeInMinutes());
                fw1.append(",");
                fw1.append("" + records1.get(i).getIteration().getTimeInMinutes());
                fw1.append(",");
                fw1.append("" + records1.get(i).getImage());
                fw1.append("\n");
            }
            fw1.flush();
            fw1.close();

            FileWriter fw2 = new FileWriter(filePath2);
            for (int i = 0; i < records2.size(); i++) {
                fw2.append("" + records2.get(i).getId());
                fw2.append(",");
                fw2.append("" + records2.get(i).getGoalId());
                fw2.append(",");
                fw2.append("" + records2.get(i).getDay());
                fw2.append(",");
                fw2.append("" + records2.get(i).getMonth());
                fw2.append(",");
                fw2.append("" + records2.get(i).getYear());
                fw2.append(",");
                fw2.append("" + records2.get(i).getEstimatedTime().getTimeInMinutes());
                fw2.append("\n");
            }
            fw2.flush();
            fw2.close();

            Toast.makeText(this, getString(R.string.settings_backup_saved) + folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.settings_error), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_REQUEST_CODE_EXPORT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportCSV();
                } else {
                    Toast.makeText(this, getString(R.string.settings_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            case STORAGE_REQUEST_CODE_IMPORT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    importCSV();
                    onResume();
                } else {
                    Toast.makeText(this, getString(R.string.settings_permission), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}