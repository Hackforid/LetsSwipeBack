package com.smilehacker.letsswipeback;

import android.app.Activity;

/**
 * Created by kleist on 2017/4/27.
 */

public class SwipeComponent {

    private Activity mActivity;
    private SwipeLayout mSwipeLayout;

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setSwipeLayout(SwipeLayout swipeLayout) {
        mSwipeLayout = swipeLayout;
    }

    public void setSwipeLayoutTranslationX(float x) {
        if (mSwipeLayout != null) {
            mSwipeLayout.setTranslationX(x);
        }
    }

    public void setActivityTranslucent(boolean translucent) {
        ActivityUtils.convertActivityToTranslucentAfterL(mActivity, new ActivityUtils.PageTranslucentListener() {
            @Override
            public void onPageTranslucent() {
                mSwipeLayout.setActivityTranslucent(true);
            }
        });
    }

}
