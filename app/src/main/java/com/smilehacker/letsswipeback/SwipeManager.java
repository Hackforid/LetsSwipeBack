package com.smilehacker.letsswipeback;

import android.app.Activity;
import android.util.SparseArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kleist on 2017/4/27.
 */

public class SwipeManager {
    private static SwipeManager mInstance;

    private SparseArray<SwipeComponent> mSwipeComponents;
    private List<SwipeComponent> mActivityStack;

    private SwipeManager() {
        mSwipeComponents = new SparseArray<>();
        mActivityStack = new LinkedList<>();
    }

    public static SwipeManager inst() {
        if (mInstance == null) {
            synchronized (SwipeManager.class) {
                if (mInstance == null) {
                    mInstance = new SwipeManager();
                }
            }
        }

        return mInstance;
    }

    public void onCreate(Activity activity) {
        SwipeComponent swipeComponent = mSwipeComponents.get(activity.hashCode());
        if (swipeComponent == null) {
            swipeComponent = new SwipeComponent();
            swipeComponent.setActivity(activity);
            mSwipeComponents.put(activity.hashCode(), swipeComponent);
        }
        mActivityStack.remove(swipeComponent);
        mActivityStack.add(swipeComponent);
    }

    public void onPostCreate(Activity activity) {
        SwipeComponent swipeComponent = mSwipeComponents.get(activity.hashCode());
        SwipeLayout swipeLayout = new SwipeLayout(activity);
        swipeLayout.attachToActivity(activity);
        swipeComponent.setSwipeLayout(swipeLayout);
    }

    public void onDestroy(Activity activity) {
        SwipeComponent swipeComponent = mSwipeComponents.get(activity.hashCode());
        if (swipeComponent != null) {
            mActivityStack.remove(swipeComponent);
            mSwipeComponents.remove(activity.hashCode());
        }
    }
}
