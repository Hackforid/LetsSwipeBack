package com.smilehacker.swipeback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kleist on 2017/4/27.
 */

public class SwipeManager {
    private static SwipeManager mInstance;

    private List<SwipePage> mPageStack;
    private List<WeakReference<Activity>> mActivityStack;

    private SwipeManager() {
        mPageStack = new ArrayList<>();
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
        SwipePage page = getPage(activity);
        if (page == null) {
            page = new SwipePage(activity);
            mPageStack.add(page);
        }
    }

    public void onPostCreate(Activity activity) {
        SwipePage page = getPage(activity);
        if (page == null) {
            return;
        }
        page.createSwipeContainer();
    }

    public void onDestroy(Activity activity) {
        SwipePage page = getPage(activity);
        if (page != null) {
            mPageStack.remove(page);
        }
    }

    public SwipePage getPage(Activity activity) {
        for (SwipePage page : mPageStack) {
            if (activity == page.getActivity()) {
                return page;
            }
        }
        return null;
    }

    public void registerApplication(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                pushActivityStack(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                removeActivityStack(activity);
            }
        });
    }

    private void pushActivityStack(Activity activity) {
        removeActivityStack(activity);
        mActivityStack.add(0, new WeakReference<Activity>(activity));
    }

    private void removeActivityStack(Activity activity) {
        Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().get() == activity) {
                iterator.remove();
                break;
            }
        }
    }

    public SwipePage getPrePage(Activity activity) {
        Activity preAct = getPreActivity(activity);
        if (preAct != null) {
            return getPage(preAct);
        } else {
            Log.i("manager", "pre act is null");
            return null;
        }
    }


    private Activity getPreActivity(Activity activity) {
        boolean findSelf = false;

        Iterator<WeakReference<Activity>> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity act = iterator.next().get();
            if (act == null) {
                iterator.remove();
            } else {
                if (findSelf) {
                    return act;
                } else if (act == activity) {
                    findSelf = true;
                }
            }
        }

        return null;
    }

}
