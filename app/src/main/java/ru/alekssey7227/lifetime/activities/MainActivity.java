package ru.alekssey7227.lifetime.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.database.DBHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolBar;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolBar);

        dbHelper = new DBHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_add_goal) {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(DBHelper.KEY_NAME, "C#");
            contentValues.put(DBHelper.KEY_TIME, "1000");
            contentValues.put(DBHelper.KEY_ITERATION, "120");

            database.insert(DBHelper.TABLE_GOALS, null, contentValues);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }
}