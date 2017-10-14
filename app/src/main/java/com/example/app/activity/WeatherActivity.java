package com.example.app.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.gson.Forecast;
import com.example.app.gson.Weather;
import com.example.app.util.HttpUtil;
import com.example.app.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by roach on 2017/10/14.
 */

public class WeatherActivity extends AppCompatActivity {
    private ScrollView mScrollView;
    private TextView titleCity;
    private TextView titleUpdate;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;


    private ImageView bingPic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT <= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );
        }else{
            getWindow().setStatusBarColor(0);
        }

        setContentView(R.layout.activity_weather);

        bingPic = (ImageView) findViewById(R.id.bingPic);
        mScrollView = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdate = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String bing_pic = preferences.getString("bing_pic", null);
        if(!TextUtils.isEmpty(bing_pic)){
            Glide.with(this).load(bing_pic).into(bingPic);
        }else{
            loadBingPic();
        }

        String weather = preferences.getString("weather", null);
        if (!TextUtils.isEmpty(weather)) {
            Weather weather1 = Utility.handleWeaherResponse(weather);
            showInfo(weather1);
        } else {
            String weather_id = getIntent().getStringExtra("weather_id");
            mScrollView.setVisibility(View.INVISIBLE);
            requestWeather(weather_id);
        }
    }

    private void loadBingPic() {
        String url = "http://guolin.tech/api/bing_pic";
        HttpUtil.setOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit().putString("bing_pic",string).apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(string).into(bingPic);
                    }
                });
            }
        });

    }

    private void requestWeather(String weather_id) {
        String url = "http://guolin.tech/api/weather?cityid=" + weather_id + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.setOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                final Weather weather = Utility.handleWeaherResponse(string);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && TextUtils.equals("ok", weather.status)) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putString("weather", string).apply();
                            showInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void showInfo(Weather weather1) {
        String cityname = weather1.mBasic.cityName;
        String updateTime = weather1.mBasic.mUpdate.updateTime.split(" ")[1];
        String temperature = weather1.mNow.temperature.concat("℃");
        String info = weather1.mNow.mMore.info;
        titleCity.setText(cityname);
        titleUpdate.setText(updateTime);
        degreeText.setText(temperature);
        weatherInfoText.setText(info);


        forecastLayout.removeAllViews();
        for (Forecast forecast : weather1.mForecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.mMore.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

        if (weather1.mAQI != null) {
            aqiText.setText(weather1.mAQI.city.aqi);
            pm25Text.setText(weather1.mAQI.city.pm25);
        }


        String cw = "洗车指数：" + weather1.mSuggestion.mCarWash.info;
        String comfort = "舒适指数：" + weather1.mSuggestion.mComfort.info;
        String sport = "运动建议：" + weather1.mSuggestion.mSport.info;
        comfortText.setText(comfort);
        sportText.setText(sport);
        carWashText.setText(cw);
    }
}
