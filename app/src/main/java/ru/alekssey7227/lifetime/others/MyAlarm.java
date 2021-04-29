package ru.alekssey7227.lifetime.others;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyAlarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "It happened", Toast.LENGTH_LONG).show();

        
    }
}
