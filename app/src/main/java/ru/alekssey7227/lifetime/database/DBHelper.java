package ru.alekssey7227.lifetime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        contentValues.put(KEY_TIME, goal.getTime().getTimeInMinutes());
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

    public void updateGoal(SQLiteDatabase db, Goal oldGoal, Goal newGoal) { //TODO: возвращать int?
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, newGoal.getName());
        contentValues.put(KEY_TIME, newGoal.getTime().getTimeInMinutes());
        contentValues.put(KEY_ITERATION, newGoal.getIteration());

        int updCount = db.update(TABLE_GOALS, contentValues, KEY_ID + "= ?", new String[]{String.valueOf(oldGoal.getId())});

        Log.d("mLog", "updates row count = " + updCount);
    }

    public void deleteGoal(SQLiteDatabase db, Goal goal) {
        int delCount = db.delete(TABLE_GOALS, KEY_ID + "=" + goal.getId(), null);

        Log.d("mLog", "deleted rows count = " + delCount);
    }

    public Goal getById(SQLiteDatabase db, int id) {
        Goal goal = null;

        Cursor cursor = db.query(TABLE_GOALS, null, "_id = ?", new String[] {Integer.toString(id)},
                null, null, null);

        if(cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            int timeIndex = cursor.getColumnIndex(KEY_TIME);
            int iterationIndex = cursor.getColumnIndex(KEY_ITERATION);

            goal = new Goal(cursor.getInt(idIndex), cursor.getString(nameIndex),
                    cursor.getInt(timeIndex), cursor.getInt(iterationIndex));
        }

        cursor.close();
        return goal;
    }
}