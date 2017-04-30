package com.smilehacker.letsswipeback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.smilehacker.swipeback.SwipeManager;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwipeManager.inst().onCreate(this);
        findViewById(R.id.btn_jump3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Test3Activity.class);
                startActivity(intent);
            }
        });
        SwipeManager.inst().getPage(this).enableSwipe(false);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeManager.inst().onPostCreate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("main", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("main", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("main", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("main", "onDestroy");
        SwipeManager.inst().onDestroy(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("main", "onStop");
    }
}
