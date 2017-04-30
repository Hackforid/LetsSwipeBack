package com.smilehacker.letsswipeback;

import android.app.Application;

import com.smilehacker.swipeback.SwipeManager;

/**
 * @author kleist
 * @create 2017/4/29.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SwipeManager.inst().registerApplication(this);
    }
}
