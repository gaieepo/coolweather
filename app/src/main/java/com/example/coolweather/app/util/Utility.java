package com.example.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.example.coolweather.app.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by gaieepo on 26/5/2016.
 */
public class Utility {

    private static List<Region> regions;

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            if (regions == null || regions.size() == 0) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getString("status").equals("ok"))
                        return false;
                    JSONArray jsonArray = jsonObject.getJSONArray("city_info");
                    if (jsonArray.length() == 0)
                        return false;
                    Gson gson = new Gson();
                    regions = gson.fromJson(jsonArray.toString(), new TypeToken<List<Region>>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            HashMap<String, String> provinceMap = new HashMap<>();
            for (Region region : regions) {
                if (!provinceMap.containsKey(region.getId().substring(5, 7))) {
                    if (region.getProv().equals("直辖市") || region.getProv().equals("特别行政区"))
                        provinceMap.put(region.getId().substring(5, 7), region.getCity());
                    else
                        provinceMap.put(region.getId().substring(5, 7), region.getProv());
                }
            }

            if (!provinceMap.isEmpty()) {
                for (Map.Entry<String, String> entry: provinceMap.entrySet()) {
                    Province province = new Province();
                    province.setProvinceCode(entry.getKey());
                    province.setProvinceName(entry.getValue());
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCitiesResponse(String code, CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            if (regions == null || regions.size() == 0) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getString("status").equals("ok"))
                        return false;
                    JSONArray jsonArray = jsonObject.getJSONArray("city_info");
                    if (jsonArray.length() == 0)
                        return false;
                    Gson gson = new Gson();
                    regions = gson.fromJson(jsonArray.toString(), new TypeToken<List<Region>>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Region region : regions) {
                if ((region.getId().substring(9, 11).equals("01") || region.getId().substring(7, 11).equals("0100")) && region.getId().substring(5, 7).equals(code)) {
                    City city = new City();
                    city.setCityCode(region.getId().substring(5, 9));
                    city.setCityName(region.getCity());
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    public static boolean handleCountiesResponse(String code, CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            if (regions == null || regions.size() == 0) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getString("status").equals("ok"))
                        return false;
                    JSONArray jsonArray = jsonObject.getJSONArray("city_info");
                    if (jsonArray.length() == 0)
                        return false;
                    Gson gson = new Gson();
                    regions = gson.fromJson(jsonArray.toString(), new TypeToken<List<Region>>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Region region : regions) {
                if (region.getId().substring(5, 9).equals(code) || (region.getId().substring(9, 11).equals("00") && region.getId().substring(5, 7).equals(code.substring(0, 2)))) {
                    County county = new County();
                    county.setCountyCode(region.getId().substring(5, 11));
                    county.setCountyName(region.getCity());
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0").getJSONObject(0);

            JSONObject basicInfo = weatherInfo.getJSONObject("basic");
            JSONObject updateInfo = basicInfo.getJSONObject("update");
            JSONObject dailyForecastInfo = weatherInfo.getJSONArray("daily_forecast").getJSONObject(0);
            JSONObject nowInfo = weatherInfo.getJSONObject("now");
            JSONObject condInfo = nowInfo.getJSONObject("cond");


            String cityName = basicInfo.getString("city");
            String publishTime = updateInfo.getString("loc").split(" ")[1];
            JSONObject maxMinTmp = dailyForecastInfo.getJSONObject("tmp");
            String temp1 = maxMinTmp.getString("max");
            String temp2 = maxMinTmp.getString("min");
            String weatherDesp = condInfo.getString("txt");
            String countyCode = basicInfo.getString("id").substring(5, 11);
            saveWeatherInfo(context, cityName, countyCode, temp1, temp2, weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName, String countyCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("county_code", countyCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
