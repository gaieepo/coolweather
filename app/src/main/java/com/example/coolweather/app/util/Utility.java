package com.example.coolweather.app.util;

import android.text.TextUtils;
import com.example.coolweather.app.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaieepo on 26/5/2016.
 */
public class Utility {

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            List<Region> regions = null;
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

            HashMap<String, String> provinceMap = new HashMap<>();
            for (Region region : regions) {
                if (!provinceMap.containsKey(region.getId().substring(5, 7))) {
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
            List<Region> regions = null;
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

            for (Region region : regions) {
                if (region.getId().substring(9, 11).equals("01") && region.getId().substring(5, 7).equals(code)) {
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
            List<Region> regions = null;
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

            for (Region region : regions) {
                if (region.getId().substring(5, 9).equals(code)) {
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
}
