package com.smilehacker.letsswipeback;

import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by kleist on 2017/4/26.
 */

public class ActivityUtils {

    public static Bitmap shot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);
        Bitmap compressedBitmap = handleBitmap(bmp);

        // 销毁缓存信息
        bmp.recycle();
        view.destroyDrawingCache();

        int rowBytes = compressedBitmap.getRowBytes();
        int height = compressedBitmap.getHeight();
        long memSize = rowBytes * height;
        Log.d("aaa", "size = " + memSize + "B");

        return compressedBitmap;
    }

    private static Bitmap bitmapToRGB565(Bitmap bitmap) {
        return bitmap.copy(Bitmap.Config.RGB_565, false);
    }

    private static Bitmap handleBitmap(Bitmap bitmap) {
        Bitmap bitmap565 = bitmapToRGB565(bitmap);
        Bitmap bitmapScaled = bitmap565.createScaledBitmap(bitmap565,
                (int) (bitmap565.getWidth() * 0.8), (int) (bitmap565.getHeight() * 0.8), false);
        bitmap565.recycle();
        return bitmapScaled;
    }



    public static boolean convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
            return true;
        } catch (Throwable t) {
            return false;
        }
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
    private static SparseArray<TranslucentListener> mTranslucentListeners = new SparseArray<>();
    public static void init() {
        TranslucentListener listener = new TranslucentListener() {
            @Override
            public void onTranslucent() {

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
    public static void convertActivityToTranslucent2(Activity activity, final TranslucentListener listener) {
        if (mTranslucentMethod == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTranslucentListeners.put(listener.hashCode(), listener);
                Object options = mGetActivityOptionsMethod.invoke(activity);
                mTranslucentMethod.invoke(activity, mObj, options);
            } else {
                mTranslucentMethod.invoke(activity, mObj);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void convertActivityToTranslucent(Activity activity, final TranslucentListener listener) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }

            MyInvocationHandler myInvocationHandler = new MyInvocationHandler(listener);
            Object obj = Proxy.newProxyInstance(Activity.class.getClassLoader(),
                    new Class[] { translucentConversionListenerClazz }, myInvocationHandler);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
                getActivityOptions.setAccessible(true);
                Object options = getActivityOptions.invoke(activity);

                Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                        translucentConversionListenerClazz, ActivityOptions.class);
                method.setAccessible(true);
                method.invoke(activity, obj, options);
            } else {
                Method method =
                        Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz);
                method.setAccessible(true);
                method.invoke(activity, obj);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
