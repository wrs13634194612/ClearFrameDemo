package com.example.myapplication;


// DrawingView.java
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {
    private Paint paint;
    private Path path;
    private List<Path> paths = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private int currentColor = Color.BLACK;
    private Bitmap canvasBitmap;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }

    private void setupPaint() {
        paint = new Paint();
        paint.setColor(currentColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, paint);
        for (int i = 0; i < paths.size(); i++) {
            paint.setColor(colors.get(i));
            canvas.drawPath(paths.get(i), paint);
        }
        paint.setColor(currentColor);
        canvas.drawPath(path, paint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                paths.add(new Path(path));
                colors.add(currentColor);
                path.reset();
                invalidate();
                break;
        }
        return true;
    }

    public void setColor(int color) {
        currentColor = color;
        invalidate();
    }

    public void undo() {
        if (!paths.isEmpty()) {
            paths.remove(paths.size() - 1);
            colors.remove(colors.size() - 1);
            invalidate();
        }
    }

    public void clear() {
        paths.clear();
        colors.clear();
        invalidate();
    }
}
