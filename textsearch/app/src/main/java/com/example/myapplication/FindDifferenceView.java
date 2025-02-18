package com.example.myapplication;




        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
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
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

public class FindDifferenceView extends View {
    private static final int ROWS = 2;  // 两行文字
    private static final int PADDING = 50;

    private String originalText;  // 原始文字
    private String modifiedText;  // 修改后的文字
    private Paint textPaint;
    private Paint highlightPaint;
    private List<Integer> differences;  // 存储不同字的位置
    private List<Integer> foundDifferences;  // 存储已找到的不同
    private float textSize;
    private RectF topTextBounds;
    private RectF bottomTextBounds;
    private OnDifferenceFoundListener listener;

    public FindDifferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.LEFT);

        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setColor(Color.YELLOW);
        highlightPaint.setStyle(Paint.Style.FILL);
        differences = new ArrayList<>();
        foundDifferences = new ArrayList<>();
        topTextBounds = new RectF();
        bottomTextBounds = new RectF();

        // 示例文本
        setTexts(
                "今天是个好日子，阳光明媚，万里无云。",
                "今天是个坏日子，阳光明媚，万里无云。"
        );
    }

    public void setTexts(String original, String modified) {
        this.originalText = original;
        this.modifiedText = modified;
        findDifferences();
        invalidate();
    }

    private void findDifferences() {
        differences.clear();
        int minLength = Math.min(originalText.length(), modifiedText.length());
        for (int i = 0; i < minLength; i++) {
            if (originalText.charAt(i) != modifiedText.charAt(i)) {
                differences.add(i);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 根据View大小调整文字大小
        textSize = (float) (w - 2 * PADDING) / originalText.length();
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float textHeight = textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top;
        float startY = PADDING + textHeight;

        // 绘制原始文本区域
        topTextBounds.set(PADDING, PADDING, getWidth() - PADDING, startY + PADDING);
        // 绘制修改文本区域
        bottomTextBounds.set(PADDING, startY + 2 * PADDING, getWidth() - PADDING, startY + 3 * PADDING);

        // 绘制高亮背景
        for (int pos : foundDifferences) {
            float startX = PADDING + pos * textSize;
            // 上面文字高亮
            canvas.drawRect(startX, topTextBounds.top, startX + textSize, topTextBounds.bottom, highlightPaint);
            // 下面文字高亮
            canvas.drawRect(startX, bottomTextBounds.top, startX + textSize, bottomTextBounds.bottom, highlightPaint);
        }

        // 绘制文字
        canvas.drawText(originalText, PADDING, topTextBounds.bottom - PADDING, textPaint);
        canvas.drawText(modifiedText, PADDING, bottomTextBounds.bottom - PADDING, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            // 计算点击的字符位置
            int charPos = (int)((x - PADDING) / textSize);
            if (charPos >= 0 && charPos < originalText.length()) {
                if (differences.contains(charPos) && !foundDifferences.contains(charPos)) {
                    // 找到一个不同
                    foundDifferences.add(charPos);
                    if (listener != null) {
                        listener.onDifferenceFound(differences.size(), foundDifferences.size());
                    }
                    invalidate();

                    // 检查是否完成
                    if (foundDifferences.size() == differences.size()) {
                        if (listener != null) {
                            listener.onAllDifferencesFound();
                        }
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public interface OnDifferenceFoundListener {
        void onDifferenceFound(int total, int found);
        void onAllDifferencesFound();
    }

    public void setOnDifferenceFoundListener(OnDifferenceFoundListener listener) {
        this.listener = listener;
    }

    public void reset() {
        foundDifferences.clear();
        invalidate();
    }
}