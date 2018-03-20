package com.smilehacker.swipeback;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kleist on 2017/4/27.
 */

public class SwipePage {

    private Activity mActivity;
    private SwipeLayout mSwipeLayout;
    private SwipePage mPrePage;
    private boolean mIsTranslucent;
    private boolean mEnableResetTranslucent = true;
    private boolean mEnablePreParallaxMove = true;

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

    public void setEnableResetTranslucent(boolean reset) {
        mEnableResetTranslucent = reset;
    }

    public void setEnablePreParallaxMove(boolean enable) {
        mEnablePreParallaxMove = enable;
    }

    public void setPreActivityContentTranslationX(float x) {
        Activity activity = SwipeManager.inst().getPreActivity(mActivity);
        if (activity != null) {
            View content = ((ViewGroup) activity.getWindow().getDecorView()).getChildAt(0);
            content.setTranslationX(x);
        }
    }

    public void setPreActivityParallaxX(float _x) {
        if (mSwipeLayout == null || !mEnablePreParallaxMove) {
            return;
        }
        int width = mSwipeLayout.getWidth();
        float x = 1f * _x / width;
        float y = 0.2f * x * x + 0.3f * x - 0.5f;
        int left = (int) (y * width);
        setPreActivityContentTranslationX(left);
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
                if (mIsTranslucent) {
                    setPreActivityContentTranslationX(0);
                    mActivity.overridePendingTransition(0, 0);
                } else {
                    mActivity.overridePendingTransition(R.anim.swipe_in, R.anim.swipe_away);
                }
            }

            @Override
            public void onTranslationX(float x) {
                setPreActivityParallaxX(x);
            }

            @Override
            public void onSwipeStart() {
                setActivityTranslucent(true);
            }

            @Override
            public void onSwipeReset() {
                if (mEnableResetTranslucent) {
                    setActivityTranslucent(false);
                }
                setPreActivityContentTranslationX(0);
            }
        });
    }

    public SwipeLayout getSwipeLayout() {
        return mSwipeLayout;
    }

    public void setSwipeEnable(boolean enable) {
        mSwipeLayout.setEnableSwipe(enable);
    }
}
