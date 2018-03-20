package com.smilehacker.letsswipeback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.smilehacker.swipeback.ActivityUtils;
import com.smilehacker.swipeback.SwipeManager;

/**
 * Created by kleist on 2017/4/27.
 */

public class Test3Activity extends BaseActivity {

    private final static String TAG = Test3Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test3);

        findViewById(R.id.btn_trans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.convertActivityToTranslucent(Test3Activity.this, new ActivityUtils.TranslucentListener() {
                    @Override
                    public void onTranslucent() {
                        Log.i("test", "--------------");
                    }
                });
            }
        });
        findViewById(R.id.btn_not_trans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.convertActivityFromTranslucent(Test3Activity.this);
            }
        });
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeManager.inst().createPage(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
