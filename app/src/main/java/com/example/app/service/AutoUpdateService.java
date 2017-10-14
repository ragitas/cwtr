package com.example.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.app.gson.Weather;
import com.example.app.util.Constants;
import com.example.app.util.HttpUtil;
import com.example.app.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by roach on 2017/10/14.
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager service = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = 8 * 60 * 60 * 1000;
        long l = SystemClock.elapsedRealtime() + time;
        Intent intent1 = new Intent(this, AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent1, 0);
        service.cancel(pendingIntent);
        service.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,l,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        String url = Constants.apiUrl+"/bing_pic";
        HttpUtil.setOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit().putString("bing_pic",string).apply();
            }
        });
    }

    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather = preferences.getString("weather", null);
        if(!TextUtils.isEmpty(weather)){
            Weather weatherObj = Utility.handleWeaherResponse(weather);
            String weatherId = weatherObj.mBasic.weatherId;
            final String s = Constants.buildUrlByCityId(weatherId);
            HttpUtil.setOkHttpRequest(s, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String string = response.body().string();
                    PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit().putString("weather",string).apply();
                }
            });
        }
    }
}
