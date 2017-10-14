package com.roa_weather.app.util;

import android.text.TextUtils;

import com.roa_weather.app.db.City;
import com.roa_weather.app.db.Country;
import com.roa_weather.app.db.Province;
import com.roa_weather.app.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by roach on 2017/10/12.
 */

public class Utility {
    /**
     * 省级数据
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++){
                    JSONObject object = array.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(object.optString("name"));
                    province.setProvinceCode(object.optInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    public static boolean handleCityResponse(String response,int proviceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++){
                    JSONObject object = array.getJSONObject(i);
                    City city = new City();
                    city.setCityName(object.optString("name"));
                    city.setCityCode(object.optInt("code"));
                    city.setProviceId(proviceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean handleCountryResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++){
                    JSONObject jsonArray = array.getJSONObject(i);
                    Country  country = new Country();
                    country.setCityId(cityId);
                    country.setCountryName(jsonArray.optString("name"));
                    country.setWeatherId(jsonArray.optString("weather_id"));
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    public static Weather handleWeaherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String s = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(s,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


}
