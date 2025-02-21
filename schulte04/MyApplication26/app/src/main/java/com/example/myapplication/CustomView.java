package com.example.myapplication;


        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.util.AttributeSet;
        import android.view.View;

public class CustomView extends View {

    private String text = "Default Text";
    private int textColor = Color.BLACK;
    private Paint paint;

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(50f);
    }

    public void setText(String text) {
        this.text = text;
        invalidate(); // 重绘View
    }

    public void setTextColor(int color) {
        this.textColor = color;
        invalidate(); // 重绘View
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(textColor);
        canvas.drawText(text, 50f, 100f, paint);
    }
}