package com.roa_weather.app.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roach on 2017/10/13.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("update")
    public Update mUpdate;

    public class Update{
        @SerializedName("loc")
        public String updateTime;

    }
}
