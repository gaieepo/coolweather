package com.example.coolweather.app.util;

/**
 * Created by gaieepo on 26/5/2016.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
