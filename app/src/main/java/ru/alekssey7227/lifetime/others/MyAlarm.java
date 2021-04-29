package ru.alekssey7227.lifetime.others;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.backend.StatsUnit;
import ru.alekssey7227.lifetime.database.GoalDBHelper;
import ru.alekssey7227.lifetime.database.StatsDBHelper;

public class MyAlarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        GoalDBHelper goalDBHelper = new GoalDBHelper(context);
        SQLiteDatabase db = goalDBHelper.getReadableDatabase();

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        StatsDBHelper statsDBHelper = new StatsDBHelper(context);
        SQLiteDatabase db2 = statsDBHelper.getWritableDatabase();


        for (Goal goal : goalDBHelper.readAllGoals(db)) {
            StatsUnit unit = statsDBHelper.get(goal.getId(), day, month, year);

            if (unit == null) {
                ContentValues cv = new ContentValues();
                cv.put(StatsDBHelper.KEY_GOAL_ID, goal.getId());
                cv.put(StatsDBHelper.KEY_DAY, day);
                cv.put(StatsDBHelper.KEY_MONTH, month);
                cv.put(StatsDBHelper.KEY_YEAR, year);
                cv.put(StatsDBHelper.KEY_ESTIMATED_TIME, 0);

                db2.insert(StatsDBHelper.TABLE_STATS, null, cv);
            }
        }
    }
}
