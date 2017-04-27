package com.smilehacker.letsswipeback;

import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
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


    public interface PageTranslucentListener {
        void onPageTranslucent();
    }

    static class MyInvocationHandler implements InvocationHandler {
        private static final String TAG = "MyInvocationHandler";
        private WeakReference<PageTranslucentListener> listener;

        public MyInvocationHandler(WeakReference<PageTranslucentListener> listener) {
            this.listener = listener;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "invoke: end time: " + System.currentTimeMillis());
            Log.d(TAG, "invoke: 被回调了");
            try {
                boolean success = (boolean) args[0];
                if (success && listener.get() != null) {
                    listener.get().onPageTranslucent();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void convertActivityToTranslucentAfterL(Activity activity, PageTranslucentListener listener) {
        Log.i("act util", "start make translucent");
        try {
            Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            getActivityOptions.setAccessible(true);
            Object options = getActivityOptions.invoke(activity);

            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }


            MyInvocationHandler myInvocationHandler = new MyInvocationHandler(new WeakReference<PageTranslucentListener>(listener));
            Object obj = Proxy.newProxyInstance(Activity.class.getClassLoader(), new Class[]{translucentConversionListenerClazz}, myInvocationHandler);

            Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz, ActivityOptions.class);
            convertToTranslucent.setAccessible(true);
            Log.d("MyInvocationHandler", "start time: " + System.currentTimeMillis());
            convertToTranslucent.invoke(activity, obj, options);
        } catch (Throwable t) {
        }
    }

}
