package com.example.app.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roach on 2017/10/13.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More mMore;

    public class More{
        @SerializedName("txt")
        public String info;

    }
}
