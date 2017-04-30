package com.smilehacker.swipeback;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;
import android.util.Log;

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
            } catch (Exception ignored) {
                ignored.printStackTrace();
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
                    new Class[] { translucentConversionListenerClazz }, myInvocationHandler);

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
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public static void convertActivityToTranslucent(Activity activity, final TranslucentListener listener) {
        if (mTranslucentMethod == null) {
            createMethod();
        }
        try {
            mLastTranslucentListener = new WeakReference<TranslucentListener>(listener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Object options = mGetActivityOptionsMethod.invoke(activity);
                mTranslucentMethod.invoke(activity, mObj, options);
            } else {
                mTranslucentMethod.invoke(activity, mObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void convertActivityToTranslucent(Activity activity, final TranslucentListener listener) {
//        try {
//            Class<?>[] classes = Activity.class.getDeclaredClasses();
//            Class<?> translucentConversionListenerClazz = null;
//            for (Class clazz : classes) {
//                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
//                    translucentConversionListenerClazz = clazz;
//                }
//            }
//
//            MyInvocationHandler myInvocationHandler = new MyInvocationHandler(listener);
//            Object obj = Proxy.newProxyInstance(Activity.class.getClassLoader(),
//                    new Class[] { translucentConversionListenerClazz }, myInvocationHandler);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
//                getActivityOptions.setAccessible(true);
//                Object options = getActivityOptions.invoke(activity);
//
//                Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
//                        translucentConversionListenerClazz, ActivityOptions.class);
//                method.setAccessible(true);
//                method.invoke(activity, obj, options);
//            } else {
//                Method method =
//                        Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz);
//                method.setAccessible(true);
//                method.invoke(activity, obj);
//            }
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//    }

}
