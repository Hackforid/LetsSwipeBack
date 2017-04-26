package com.smilehacker.letsswipeback;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by kleist on 2017/4/26.
 */

public class SwipeBackContainer extends FrameLayout {

    private final static String TAG = SwipeBackContainer.class.getSimpleName();

    private ViewDragHelper mDragHelper;
    private View mContentView;
    private ImageView mPreviewView;
    private SwipeBackListener mListener;
    private boolean mShouldFinish;

    public SwipeBackContainer(@NonNull Context context) {
        super(context);
        init();
    }

    public SwipeBackContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeBackContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPreviewView = (ImageView) getChildAt(0);
        mContentView = getChildAt(1);
        mPreviewView.setImageBitmap(SwipeBackManager.inst().getLastActivityShot());
    }

    private void refreshPreviewPostion() {
        int contentLeft = mContentView.getLeft();
        float x = 1f * contentLeft / getWidth();
        float y = 0.2f * x * x + 0.3f * x - 0.5f;
        int left = (int) (y * getWidth());
        Log.d(TAG, "content left=" + contentLeft + " x=" + x + " y=" + y + " left=" + left);
        mPreviewView.setLeft(left);
    }

    private int mLastX;

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return false;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                mLastX = left;
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return super.clampViewPositionVertical(child, top, dy);
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (releasedChild != mContentView) {
                    return;
                }

                if (mLastX > getWidth() / 5) {
                    mShouldFinish = true;
                    mDragHelper.settleCapturedViewAt(getWidth(), 0);
                    invalidate();
                } else {
                    mDragHelper.settleCapturedViewAt(0, 0);
                    invalidate();
                }

            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                super.onEdgeDragStarted(edgeFlags, pointerId);
            }

            @Override
            public void onEdgeTouched(int edgeFlags, int pointerId) {
                // 触摸边界时cap contentview
                mDragHelper.captureChildView(mContentView, pointerId);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
//                Log.d(TAG, changedView + " left=" + left + " dx=" + dx);
                if (changedView == mContentView) {
                    refreshPreviewPostion();
                }
            }
        });
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }


    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        } else {
            if (mShouldFinish) {
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public interface SwipeBackListener {
        void onFinish();
    }

    public void setSwipeBackListener(SwipeBackListener listener) {
        mListener = listener;
    }

}
