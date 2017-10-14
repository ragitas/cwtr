package com.roa_weather.app.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by roach on 2017/10/13.
 */

public class Weather {
    public String status;

    @SerializedName("basic")
    public Basic mBasic;

    @SerializedName("aqi")
    public AQI mAQI;

    @SerializedName("now")
    public Now mNow;

    @SerializedName("suggestion")
    public Suggestion mSuggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> mForecastList;
}
