package com.roa_weather.app.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roach on 2017/10/13.
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More mMore;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }


}
