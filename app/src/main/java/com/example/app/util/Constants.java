package com.example.app.util;

/**
 * Created by roach on 2017/10/14.
 */

public class Constants {
    public static String baseUrl = "http://guolin.tech";
    public static String apiUrl = baseUrl+"/api";

    public static String buildUrlByCityId(String cityId) {
        StringBuilder builder = new StringBuilder(apiUrl);
        builder.append("/weather?cityid=");
        builder.append(cityId);
        builder.append("&key=bc0418b57b2d4918819d3974ac1285d9");
        return builder.toString();
    }
}
