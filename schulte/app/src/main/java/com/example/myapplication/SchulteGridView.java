package com.example.myapplication;



        import android.animation.ValueAnimator;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Point;
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
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;
public class SchulteGridView extends View {
    private static final int GRID_SIZE = 5; // 5x5网格
    private static final int TOTAL_NUMBERS = GRID_SIZE * GRID_SIZE;

    private Paint cellPaint;
    private Paint textPaint;
    private Paint highlightPaint;
    private float cellSize;
    private int[][] grid;
    private boolean[][] highlighted;
    private int currentNumber = 1;
    private long startTime;
    private OnGameCompleteListener listener;
    private ValueAnimator clickAnimator;
    private Point lastClickedCell;

    public SchulteGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        cellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cellPaint.setColor(Color.WHITE);
        cellPaint.setStyle(Paint.Style.FILL);
        cellPaint.setStrokeWidth(2f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);

        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setColor(Color.YELLOW);
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setAlpha(100);

        grid = new int[GRID_SIZE][GRID_SIZE];
        highlighted = new boolean[GRID_SIZE][GRID_SIZE];

        setupClickAnimation();
        initializeGrid();
    }

    private void setupClickAnimation() {
        clickAnimator = ValueAnimator.ofFloat(0f, 1f);
        clickAnimator.setDuration(300);
        clickAnimator.addUpdateListener(animation -> {
            invalidate();
        });
    }

    private void initializeGrid() {
        // 生成1到25的数字列表
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= TOTAL_NUMBERS; i++) {
            numbers.add(i);
        }

        // 随机打乱数字
        Collections.shuffle(numbers);

        // 填充网格
        int index = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = numbers.get(index++);
                highlighted[i][j] = false;
            }
        }

        currentNumber = 1;
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = Math.min(w, h) / (float) GRID_SIZE;
        textPaint.setTextSize(cellSize * 0.4f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制网格
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                float left = j * cellSize;
                float top = i * cellSize;

                // 绘制单元格背景
                if (highlighted[i][j]) {
                    canvas.drawRect(left, top, left + cellSize, top + cellSize, highlightPaint);
                }
                canvas.drawRect(left, top, left + cellSize, top + cellSize, cellPaint);

                // 绘制数字
                String number = String.valueOf(grid[i][j]);
                float x = left + cellSize / 2;
                float y = top + cellSize / 2 + textPaint.getTextSize() / 3;
                canvas.drawText(number, x, y, textPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int col = (int) (x / cellSize);
            int row = (int) (y / cellSize);

            if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                checkNumber(row, col);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void checkNumber(int row, int col) {
        if (grid[row][col] == currentNumber) {
            // 点击正确
            highlighted[row][col] = true;
            lastClickedCell = new Point(col, row);
            clickAnimator.start();

            currentNumber++;
            if (currentNumber > TOTAL_NUMBERS) {
                // 游戏完成
                long endTime = System.currentTimeMillis();
                float timeUsed = (endTime - startTime) / 1000f;
                if (listener != null) {
                    listener.onGameComplete(timeUsed);
                }
            }
        } else {
            // 点击错误，显示提示
            if (listener != null) {
                listener.onWrongNumber();
            }
        }
        invalidate();
    }

    public interface OnGameCompleteListener {
        void onGameComplete(float timeUsed);
        void onWrongNumber();
    }

    public void setOnGameCompleteListener(OnGameCompleteListener listener) {
        this.listener = listener;
    }

    public void reset() {
        initializeGrid();
        invalidate();
    }

    public int getCurrentNumber() {
        return currentNumber;
    }
}