package com.smilehacker.swipeback;

import android.app.Activity;
import android.util.Log;

/**
 * Created by kleist on 2017/4/27.
 */

public class SwipePage {

    private Activity mActivity;
    private SwipeLayout mSwipeLayout;
    private SwipePage mPrePage;
    private boolean mIsTranslucent;

    public SwipePage(Activity activity) {
        mActivity = activity;
        mSwipeLayout = new SwipeLayout(mActivity);
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setSwipeLayoutTranslationX(float x) {
        if (mSwipeLayout != null) {
            mSwipeLayout.setTranslationX(x);
        }
    }

    public void setSwipeLayoutParallaxX(float _x) {
        if (mSwipeLayout == null) {
            return;
        }
        int width = mSwipeLayout.getWidth();
        float x = 1f * _x / width;
        float y = 0.2f * x * x + 0.3f * x - 0.5f;
        int left = (int) (y * width);
        mSwipeLayout.setTranslationX(left);
    }

    public void setActivityTranslucent(boolean translucent) {
        if (translucent) {
            if (!mIsTranslucent) {
                ActivityUtils.convertActivityToTranslucent(mActivity, new ActivityUtils.TranslucentListener() {
                    @Override
                    public void onTranslucent() {
                        Log.i(SwipePage.this.toString(), "onTranslucent");
                        mIsTranslucent = true;
                        mSwipeLayout.setActivityTranslucent(true);
                    }
                });
            }
        } else {
            if (mIsTranslucent) {
                ActivityUtils.convertActivityFromTranslucent(mActivity);
                mSwipeLayout.setActivityTranslucent(false);
                mIsTranslucent = false;
            }
        }
    }

    public void createSwipeContainer() {
        mSwipeLayout.attachToActivity(mActivity);
        mSwipeLayout.setListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onSwipeFinish() {
                mActivity.finish();
                mActivity.overridePendingTransition(0, 0);
                if (mPrePage != null) {
                    mPrePage.setSwipeLayoutTranslationX(0);
                }
            }

            @Override
            public void onTranslationX(float x) {
                if (mPrePage != null) {
                    mPrePage.setSwipeLayoutParallaxX(x);
                }
            }

            @Override
            public void onSwipeStart() {
                setActivityTranslucent(true);
                mPrePage = SwipeManager.inst().getPrePage(mActivity);
            }

            @Override
            public void onSwipeReset() {
                setActivityTranslucent(false);
                if (mPrePage != null) {
                    mPrePage.setSwipeLayoutTranslationX(0);
                }
            }
        });
    }

    public SwipeLayout getSwipeLayout() {
        return mSwipeLayout;
    }

    public void enableSwipe(boolean enable) {
        mSwipeLayout.setEnableSwipe(enable);
    }
}
