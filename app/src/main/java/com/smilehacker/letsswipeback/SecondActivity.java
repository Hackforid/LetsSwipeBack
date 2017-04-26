package com.smilehacker.letsswipeback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

/**
 * Created by kleist on 2017/4/26.
 */

public class SecondActivity extends BaseActivity {
    SwipeBackContainer mContainer;
    ImageView mBg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        mContainer = (SwipeBackContainer) findViewById(R.id.container);
        mBg = (ImageView) findViewById(R.id.bg);
        mContainer.setSwipeBackListener(new SwipeBackContainer.SwipeBackListener() {
            @Override
            public void onFinish() {
                finish();
                overridePendingTransition(0, R.anim.just_out);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
