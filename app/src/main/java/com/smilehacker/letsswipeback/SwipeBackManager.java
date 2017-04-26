package com.smilehacker.letsswipeback;

import android.app.Activity;
import android.graphics.Bitmap;

import java.lang.ref.SoftReference;

/**
 * Created by kleist on 2017/4/26.
 */

public class SwipeBackManager {
    private static SwipeBackManager mInstance;
    private SoftReference<Bitmap> mLastActivityShot;

    public static SwipeBackManager inst() {
        if (mInstance == null) {
            synchronized (SwipeBackManager.class) {
                if (mInstance == null) {
                    mInstance = new SwipeBackManager();
                }
            }
        }
        return mInstance;
    }

    private SwipeBackManager() {

    }

    public void onLastActivityStop(Activity activity) {
        mLastActivityShot = new SoftReference<>(ActivityUtils.shot(activity));
    }

    public Bitmap getLastActivityShot() {
        return mLastActivityShot != null ? mLastActivityShot.get() : null;
    }
}
