package com.smilehacker.letsswipeback;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by kleist on 2017/4/27.
 */

public class Test3Activity extends BaseActivity {

    private final static String TAG = Test3Activity.class.getSimpleName();
    private SwipeLayout mContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test3);
        mContainer = (SwipeLayout) findViewById(R.id.container);
        mContainer.setListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onSwipeAway() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i("test3", "post create");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.convertActivityToTranslucentAfterL(Test3Activity.this, new ActivityUtils.PageTranslucentListener() {
                    @Override
                    public void onPageTranslucent() {
                        Log.i("Test3", "set success");
                        mContainer.setActivityTranslucent(true);
                    }
                });
            }
        }, 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onstart");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
