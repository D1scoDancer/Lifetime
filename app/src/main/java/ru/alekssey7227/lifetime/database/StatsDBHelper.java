package ru.alekssey7227.lifetime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.backend.StatsUnit;

public class StatsDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "statsDB";
    public static final String TABLE_STATS = "stats";

    public static final String KEY_ID = "_id";
    public static final String KEY_GOAL_ID = "goal_id";
    public static final String KEY_DAY = "day";
    public static final String KEY_ESTIMATED_TIME = "estimated_time";

    public StatsDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_STATS + "(" + KEY_ID
                + " integer primary key, " + KEY_GOAL_ID + " integer, " + KEY_DAY + " bigint, "
                + KEY_ESTIMATED_TIME + " integer" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_STATS);

        onCreate(db);
    }

    public void updateStatsUnit(SQLiteDatabase db, StatsUnit unit) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_GOAL_ID, unit.getGoalId());
        contentValues.put(KEY_DAY, unit.getDay());
        contentValues.put(KEY_ESTIMATED_TIME, unit.getEstimatedTime());

        int updCount = db.update(TABLE_STATS, contentValues, KEY_ID + "= ?", new String[]{String.valueOf(unit.getId())});

        Log.d("mLog", "updates row count = " + updCount);
    }

    public void deleteStatsUnit(SQLiteDatabase db, StatsUnit unit) {
        int delCount = db.delete(TABLE_STATS, KEY_ID + "=" + unit.getId(), null);

        Log.d("mLog", "deleted rows count = " + delCount);
    }

    public StatsUnit getOrCreate(int goal_id, long day) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_STATS, new String[]{KEY_ID, KEY_GOAL_ID, KEY_ESTIMATED_TIME, KEY_ESTIMATED_TIME},
                KEY_GOAL_ID + "=? and " + KEY_DAY + "=?",
                new String[]{Integer.toString(goal_id), Long.toString(day)}, null,
                null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int goalIdIndex = cursor.getColumnIndex(KEY_GOAL_ID);
            int dayIndex = cursor.getColumnIndex(KEY_DAY);
            int estimatedTimeIndex = cursor.getColumnIndex(KEY_ESTIMATED_TIME);

            return new StatsUnit(cursor.getInt(idIndex), cursor.getInt(goalIdIndex),
                    cursor.getLong(dayIndex), cursor.getInt(estimatedTimeIndex));

        } else {
            return new StatsUnit(-1, goal_id, day, 0);
        }
    }
}
