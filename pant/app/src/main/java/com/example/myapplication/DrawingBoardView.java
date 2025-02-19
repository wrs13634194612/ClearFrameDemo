package com.example.myapplication;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class DrawingBoardView extends View {
    private static final float TOUCH_TOLERANCE = 4;

    private Paint paint;
    private Path currentPath;
    private float lastX, lastY;
    private List<DrawPath> paths;
    private Stack<DrawPath> undoStack;

    // 保存路径和画笔属性
    private static class DrawPath {
        Path path;
        Paint paint;

        DrawPath(Path path, Paint paint) {
            this.path = path;
            this.paint = new Paint(paint); // 复制画笔属性
        }
    }

    public DrawingBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paths = new ArrayList<>();
        undoStack = new Stack<>();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(6f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制所有已保存的路径
        for (DrawPath drawPath : paths) {
            canvas.drawPath(drawPath.path, drawPath.paint);
        }
        // 绘制当前正在绘制的路径
        if (currentPath != null) {
            canvas.drawPath(currentPath, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }
        invalidate();
        return true;
    }

    private void touchStart(float x, float y) {
        currentPath = new Path();
        currentPath.moveTo(x, y);
        lastX = x;
        lastY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - lastX);
        float dy = Math.abs(y - lastY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // 使用二次贝塞尔曲线使线条更平滑
            currentPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
            lastX = x;
            lastY = y;
        }
    }

    private void touchUp() {
        if (currentPath != null) {
            currentPath.lineTo(lastX, lastY);
            // 保存路径和画笔属性
            paths.add(new DrawPath(currentPath, paint));
            currentPath = null;
            // 清空撤销栈
            undoStack.clear();
        }
    }

    // 设置画笔颜色
    public void setColor(int color) {
        paint = new Paint(paint);
        paint.setColor(color);
    }

    // 设置画笔宽度
    public void setStrokeWidth(float width) {
        paint = new Paint(paint);
        paint.setStrokeWidth(width);
    }

    // 设置橡皮擦模式
    public void setEraserMode(boolean isEraser) {
        paint = new Paint(paint);
        if (isEraser) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            paint.setXfermode(null);
        }
    }

    // 撤销
    public void undo() {
        if (!paths.isEmpty()) {
            undoStack.push(paths.remove(paths.size() - 1));
            invalidate();
        }
    }

    // 重做
    public void redo() {
        if (!undoStack.isEmpty()) {
            paths.add(undoStack.pop());
            invalidate();
        }
    }

    // 清除画板
    public void clear() {
        paths.clear();
        undoStack.clear();
        currentPath = null;
        invalidate();
    }

    // 保存为图片
    public Bitmap save() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        draw(canvas);
        return bitmap;
    }
}