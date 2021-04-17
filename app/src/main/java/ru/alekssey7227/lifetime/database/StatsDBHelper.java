package ru.alekssey7227.lifetime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

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
}
