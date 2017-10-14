package com.roa_weather.app.util;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by roach on 2017/10/12.
 */

public class HttpUtil {
    public static void setOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request build = new Request.Builder().url(address).build();
        client.newCall(build).enqueue(callback);

        Log.d("roach","url:"+address);
    }
}
