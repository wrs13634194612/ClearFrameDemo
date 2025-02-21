package com.example.myapplication;

import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.drawable.Drawable;

public class DrawableUtils {

    /**
     * 将Drawable转换为Bitmap
     *
     * @param drawable 要转换的Drawable对象
     * @return 转换后的Bitmap对象
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 获取Drawable的宽度和高度
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        // 创建一个空的Bitmap对象
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // 创建一个Canvas对象，并将Bitmap绑定到Canvas
        Canvas canvas = new Canvas(bitmap);

        // 设置Drawable的边界
        drawable.setBounds(0, 0, width, height);

        // 将Drawable绘制到Canvas上
        drawable.draw(canvas);

        return bitmap;
    }
}