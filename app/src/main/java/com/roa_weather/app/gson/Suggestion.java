package com.roa_weather.app.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roach on 2017/10/13.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort mComfort;

    @SerializedName("cw")
    public CarWash mCarWash;

    @SerializedName("sport")
    public Sport mSport;


    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
