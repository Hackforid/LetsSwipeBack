package com.smilehacker.letsswipeback;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by kleist on 2017/4/27.
 */

public class SwipeLayout extends FrameLayout {

    private final static String TAG = SwipeLayout.class.getSimpleName();

    private ViewConfiguration mViewConfiguration;

    private boolean mEnableSwipe;
    private float mLastX;
    private float mLastY;
    private boolean mHasTouchEdge;
    private View mContentView;

    private boolean mAnimContentView;
    private boolean mIsFinish;
    private boolean mIsActivityTranslucent = false;

    private SwipeListener mSwipeListener;


    public SwipeLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mViewConfiguration = ViewConfiguration.get(context);
    }

    public void setEnableSwipe(boolean enableSwipe) {
        mEnableSwipe = enableSwipe;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;

        int action = ev.getAction();
        Log.i(TAG, "intercept action=" + ev.getAction());
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mHasTouchEdge = isEdgeActionDown(ev);
                Log.i(TAG, "has touch edge=" + mHasTouchEdge);
                if (mHasTouchEdge && mSwipeListener != null) {
                    mSwipeListener.onStartSwipe();
                }
                mLastX = ev.getX();
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "action move");
                Log.i(TAG, "is edge drag " + isEdgeDrag(ev));
                if (mHasTouchEdge && isEdgeDrag(ev)) {
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHasTouchEdge = false;
        }

        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        boolean handled = true;

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!isEnable()) {
                    handled = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mHasTouchEdge && isEdgeDrag(ev)) {
                    moveContentView(ev.getX());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                checkReleaseEvent(ev.getX());
                mHasTouchEdge = false;
        }

        return handled;

    }

    private boolean isEdgeDrag(MotionEvent ev) {
        return (Math.abs(ev.getX() - mLastX) > Math.abs(ev.getY() - mLastY));
    }

    private boolean isEdgeActionDown(MotionEvent ev) {
        return (ev.getX() < mViewConfiguration.getScaledEdgeSlop());
    }

    private void moveContentView(float position) {
        if (!mIsActivityTranslucent) {
            return;
        }
        mContentView.setTranslationX(position);
        if (mSwipeListener != null) {
            mSwipeListener.onTranslationX(position);
        }
    }

    private void checkReleaseEvent(float position) {
        if (position < getWidth() / 4) {
            resetContentView();
        } else {
            finishContentView();
        }
    }

    private void resetContentView() {
        if (mContentView == null) {
            return;
        }
        final float startX = mContentView.getTranslationX();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, startX);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float left = startX - (float) animation.getAnimatedValue();
                mContentView.setTranslationX(left);
                if (mSwipeListener != null) {
                    mSwipeListener.onTranslationX(left);
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimContentView = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mAnimContentView = false;
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    private void finishContentView() {
        if (mContentView == null) {
            return;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mContentView.getTranslationX(), mContentView.getWidth());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float left = (float) animation.getAnimatedValue();
                mContentView.setTranslationX(left);
                if (mSwipeListener != null) {
                    mSwipeListener.onTranslationX(left);
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimContentView = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mAnimContentView = false;
                        callFinish();
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    private void callFinish() {
        mIsFinish = true;
        if (mSwipeListener != null) {
            mSwipeListener.onSwipeAway();
        }
    }

    private boolean isEnable() {
        return !mAnimContentView && !mIsFinish;
    }

    public interface SwipeListener {
        void onSwipeAway();
        void onTranslationX(float x);
        void onStartSwipe();
    }

    public void setActivityTranslucent(boolean translucent) {
        mIsActivityTranslucent = translucent;
    }

    public void setListener(SwipeListener listener) {
        mSwipeListener = listener;
    }

    public void attachToActivity(Activity activity) {
        if (getParent() != null){
            return;
        }

        ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(lp);

        TypedArray theme  = activity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = theme.getResourceId(0, 0);
        theme.recycle();

        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View contentView = decorView.getChildAt(0);
        contentView.setBackgroundResource(background);
        decorView.removeView(contentView);
        addView(contentView);
        mContentView = contentView;
        decorView.addView(this);
    }
}
