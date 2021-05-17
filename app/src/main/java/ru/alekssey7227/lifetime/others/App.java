package ru.alekssey7227.lifetime.others;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
