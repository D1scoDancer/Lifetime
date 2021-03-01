package ru.alekssey7227.lifetime.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

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

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            // action with ID action_settings was selected
//            case R.id.action_settings:
//                // this is where you put your own code to do what you want.
//                break;
//            default:
//                break;
//        }
//        return true;
//    }

    @Override
    public void onClick(View v) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

//        if (v.getId() == R.id.btnAdd) {
//            contentValues.put(DBHelper.KEY_NAME, "C#");
//            contentValues.put(DBHelper.KEY_TIME, "1000");
//            contentValues.put(DBHelper.KEY_ITERATION, "120");
//
//            database.insert(DBHelper.TABLE_GOALS, null, contentValues);
//        }  // см урок 34
    }
}