package com.example.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchulteGridView extends View {
    private   int GRID_SIZE=9; // 5x5网格
    private  int TOTAL_NUMBERS=81;
    private Paint textPaint;
    private float cellSize;
    private float cellSpacing; // 单元格间距
    private int[][] grid;
    private boolean[][] highlighted;
    private int currentNumber = 1;
    private long startTime;
    private OnGameCompleteListener listener;
    private ValueAnimator clickAnimator;
    private Point lastClickedCell;
    private Paint selectedTextPaint; // 添加选中状态的文字画笔
    private Bitmap cellBitmap; // 单元格的位图
    private Bitmap selectedBitmap; // 选中状态的位图

    private Context context;

    private MediaPlayer correctSoundPlayer; // 正确点击音效
    private MediaPlayer wrongSoundPlayer; // 错误点击音效

    public SchulteGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // 添加选中状态的文字画笔
        selectedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedTextPaint.setColor(Color.BLACK); // 选中后的文字颜色为蓝色
        selectedTextPaint.setTextAlign(Paint.Align.CENTER);

        grid = new int[GRID_SIZE][GRID_SIZE];
        highlighted = new boolean[GRID_SIZE][GRID_SIZE];

        // 初始化音效播放器
        correctSoundPlayer = MediaPlayer.create(context, R.raw.clear_1); // 正确点击音效
        wrongSoundPlayer = MediaPlayer.create(context, R.raw.clear_3); // 错误点击音效
        correctSoundPlayer.setVolume(1.0f, 1.0f);
        wrongSoundPlayer.setVolume(1.0f, 1.0f);


        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.circle_bg);
        Drawable drawable2 = ContextCompat.getDrawable(context, R.drawable.circle_green);

        cellBitmap = DrawableUtils.drawableToBitmap(drawable);
        selectedBitmap = DrawableUtils.drawableToBitmap(drawable2);


        //  cellBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle_bg); // 请确保有此位图资源
        //  selectedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_online); // 选中状态的位图

        //  cellSpacing = dpToPx(GRID_DIS); // 将50dp转换为像素
        setupClickAnimation();
        initializeGrid();
    }

    private void setupClickAnimation() {
        clickAnimator = ValueAnimator.ofFloat(0f, 1f);
        clickAnimator.setDuration(300);
        clickAnimator.addUpdateListener(animation -> invalidate());
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
        // 计算单元格大小，考虑间距
        cellSize = (Math.min(w, h) - (cellSpacing * (GRID_SIZE + 1))) / (float) GRID_SIZE;
        textPaint.setTextSize(cellSize * 0.4f);
        selectedTextPaint.setTextSize(cellSize * 0.4f); // 设置选中状态文字大小
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 计算绘制起点，使第一个单元格居中
        float startX = (getWidth() - (GRID_SIZE * cellSize + (GRID_SIZE + 1) * cellSpacing)) / 2;
        float startY = (getHeight() - (GRID_SIZE * cellSize + (GRID_SIZE + 1) * cellSpacing)) / 2;

        // 绘制网格
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                float left = startX + j * (cellSize + cellSpacing); // 添加间距
                float top = startY + i * (cellSize + cellSpacing); // 添加间距

                // 绘制单元格背景
                if (highlighted[i][j]) {
                    // 绘制选中状态的位图
                    canvas.drawBitmap(selectedBitmap, null, new android.graphics.RectF(left, top, left + cellSize, top + cellSize), null);
                } else {
                    // 绘制默认状态的位图
                    canvas.drawBitmap(cellBitmap, null, new android.graphics.RectF(left, top, left + cellSize, top + cellSize), null);
                }

                // 绘制数字
                String number = String.valueOf(grid[i][j]);
                float x = left + cellSize / 2;
                float y = top + cellSize / 2 + textPaint.getTextSize() / 3;

                if (highlighted[i][j]) {
                    canvas.drawText(number, x, y, selectedTextPaint);
                } else {
                    canvas.drawText(number, x, y, textPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            // 计算相对于居中位置的列和行
            int col = (int) ((x - ((getWidth() - (GRID_SIZE * cellSize + (GRID_SIZE + 1) * cellSpacing)) / 2)) / (cellSize + cellSpacing));
            int row = (int) ((y - ((getHeight() - (GRID_SIZE * cellSize + (GRID_SIZE + 1) * cellSpacing)) / 2)) / (cellSize + cellSpacing));

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
            playSound(correctSoundPlayer); // 播放正确点击音效
        } else {
            // 点击错误，显示提示
            if (listener != null) {
                listener.onWrongNumber();
            }
            playSound(wrongSoundPlayer); // 播放错误点击音效
        }
        invalidate();
    }

    private void playSound(MediaPlayer player) {
        if (player != null) {
            player.seekTo(0); // 重置音效到开头
            player.start(); // 播放音效
        }
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
    public void setText(int age,int num,float dis) {
        this.GRID_SIZE = age;
        this.TOTAL_NUMBERS = num;
        this.cellSpacing = dis;
        invalidate(); // 重绘View
    }
    public int getCurrentNumber() {
        return currentNumber;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 释放音效播放器资源
        if (correctSoundPlayer != null) {
            correctSoundPlayer.release();
            correctSoundPlayer = null;
        }
        if (wrongSoundPlayer != null) {
            wrongSoundPlayer.release();
            wrongSoundPlayer = null;
        }
    }

}