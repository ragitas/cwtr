package com.example.app;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by roach on 2017/10/12.
 */

public class App extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        LitePal.initialize(this);
    }

    public static Context getAppContext(){
        return sContext;
    }
}
