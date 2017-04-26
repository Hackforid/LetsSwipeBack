package com.smilehacker.letsswipeback;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.View;

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

}
