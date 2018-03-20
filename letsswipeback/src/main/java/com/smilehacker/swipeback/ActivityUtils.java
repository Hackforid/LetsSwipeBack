package com.smilehacker.swipeback;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by kleist on 2017/4/26.
 */

public class ActivityUtils {
    private static Method convertActivityFromTranslucentMethod;
    private static final String TAG = ActivityUtils.class.getSimpleName();


    public static boolean convertActivityFromTranslucent(Activity activity) {
        if (convertActivityFromTranslucentMethod == null) {
            try {
                convertActivityFromTranslucentMethod = Activity.class.getDeclaredMethod("convertFromTranslucent");
                convertActivityFromTranslucentMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "can't get method", e);
                return false;
            }
        }
        try {
            convertActivityFromTranslucentMethod.invoke(activity);
            return true;
        } catch (Exception  e) {
            Log.e(TAG, "can't invoke method", e);
        }
        return false;
    }

    public interface TranslucentListener {
        void onTranslucent();
    }

    private static class MyInvocationHandler implements InvocationHandler {
        private TranslucentListener listener;

        MyInvocationHandler(TranslucentListener listener) {
            this.listener = listener;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                boolean success = (boolean) args[0];
                if (success && listener != null) {
                    listener.onTranslucent();
                }
            } catch (Exception e) {
                Log.e(TAG, "invoke method", e);
            }
            return null;
        }
    }

    private static Method mTranslucentMethod;
    private static Method mGetActivityOptionsMethod;
    private static Object mObj;
    private static WeakReference<TranslucentListener> mLastTranslucentListener;

    public static void createMethod() {
        TranslucentListener listener = new TranslucentListener() {
            @Override
            public void onTranslucent() {
                if (mLastTranslucentListener != null && mLastTranslucentListener.get() != null) {
                    mLastTranslucentListener.get().onTranslucent();
                }
            }
        };
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }

            MyInvocationHandler myInvocationHandler = new MyInvocationHandler(listener);
            mObj = Proxy.newProxyInstance(Activity.class.getClassLoader(),
                    new Class[] { translucentConversionListenerClazz },
                    myInvocationHandler);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mGetActivityOptionsMethod = Activity.class.getDeclaredMethod("getActivityOptions");
                mGetActivityOptionsMethod.setAccessible(true);

                Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                        translucentConversionListenerClazz, ActivityOptions.class);
                method.setAccessible(true);
                mTranslucentMethod = method;
            } else {
                Method method =
                        Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz);
                method.setAccessible(true);
                mTranslucentMethod = method;
            }
        } catch (Exception e) {
            Log.e(TAG, "createMethod", e);
        }
    }
    public static void convertActivityToTranslucent(Activity activity, final TranslucentListener listener) {
        if (mTranslucentMethod == null) {
            createMethod();
        }
        try {
            mLastTranslucentListener = new WeakReference<>(listener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Object options = mGetActivityOptionsMethod.invoke(activity);
                mTranslucentMethod.invoke(activity, mObj, options);
            } else {
                mTranslucentMethod.invoke(activity, mObj);
            }
        } catch (Exception e) {
            Log.e(TAG, "convertActivityToTranslucent", e);
        }
    }

    public static Activity getActivityFromView(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

}
