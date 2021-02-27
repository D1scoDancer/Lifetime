package ru.alekssey7227.lifetime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.alekssey7227.lifetime.backend.Goal;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "goalDB";
    public static final String TABLE_GOALS = "goals";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TIME = "time";
    public static final String KEY_ITERATION = "iteration";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: compute constant value of ...
        db.execSQL("create table " + TABLE_GOALS + "(" + KEY_ID
                + " integer primary key, " + KEY_NAME + " text, " + KEY_TIME + " integer, "
                + KEY_ITERATION + " integer" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_GOALS);

        onCreate(db);
    }

    public long addGoal(SQLiteDatabase db, Goal goal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, goal.getName());
        contentValues.put(KEY_TIME, goal.getTime());
        contentValues.put(KEY_ITERATION, goal.getIteration());

        return db.insert(TABLE_GOALS, null, contentValues);
    }

    public List<Goal> readAllGoals(SQLiteDatabase db) {
        List<Goal> goalList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_GOALS, null, null, null,
                null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            int timeIndex = cursor.getColumnIndex(KEY_TIME);
            int iterationIndex = cursor.getColumnIndex(KEY_ITERATION);

            do {
                Goal goal = new Goal(cursor.getInt(idIndex), cursor.getString(nameIndex),
                        cursor.getInt(timeIndex), cursor.getInt(iterationIndex));

                goalList.add(goal);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return goalList;
    }

    public void deleteGoal(SQLiteDatabase db, Goal goal) {

    }

    public void updateGoal(SQLiteDatabase db, Goal oldGoal, Goal newGoal) {
        
    }


}