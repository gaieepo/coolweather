package com.example.coolweather.app.util;

import android.text.TextUtils;
import com.example.coolweather.app.model.CoolWeatherDB;

/**
 * Created by gaieepo on 26/5/2016.
 */
public class Utility {
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            // TODO
            return true;
        }
        return false;
    }

    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            // TODO
            return true;
        }
        return false;
    }

    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            // TODO
            return true;
        }
        return false;
    }
}
