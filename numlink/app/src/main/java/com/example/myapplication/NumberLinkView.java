package com.example.myapplication;




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
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Random;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

public class NumberLinkView extends View {
    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final int MAX_NUMBER = 9;

    private Cell[][] grid;
    private Paint cellPaint;
    private Paint textPaint;
    private Paint linePaint;
    private float cellSize;
    private Cell selectedCell;
    private List<Point> currentPath;
    private List<List<Point>> completedPaths;
    private OnGameCompleteListener listener;

    private static class Cell {
        int number;
        boolean isSelected;
        boolean isConnected;

        Cell(int number) {
            this.number = number;
            this.isSelected = false;
            this.isConnected = false;
        }
    }

    public NumberLinkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        cellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(8f);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        grid = new Cell[ROWS][COLS];
        currentPath = new ArrayList<>();
        completedPaths = new ArrayList<>();

        initializeGrid();
    }

    private void initializeGrid() {
        // 初始化空网格
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = new Cell(0);
            }
        }

        // 放置配对的数字
        Random random = new Random();
        for (int number = 1; number <= MAX_NUMBER; number++) {
            for (int pair = 0; pair < 2; pair++) {
                while (true) {
                    int row = random.nextInt(ROWS);
                    int col = random.nextInt(COLS);
                    if (grid[row][col].number == 0) {
                        grid[row][col].number = number;
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = Math.min(w / (float) COLS, h / (float) ROWS);
        textPaint.setTextSize(cellSize * 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
        drawNumbers(canvas);
        drawCompletedPaths(canvas);
        drawCurrentPath(canvas);
    }

    private void drawGrid(Canvas canvas) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                float left = j * cellSize;
                float top = i * cellSize;

                cellPaint.setColor(Color.WHITE);
                cellPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(left, top, left + cellSize, top + cellSize, cellPaint);

                cellPaint.setColor(Color.GRAY);
                cellPaint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(left, top, left + cellSize, top + cellSize, cellPaint);

                if (grid[i][j].isSelected) {
                    cellPaint.setColor(Color.YELLOW);
                    cellPaint.setStyle(Paint.Style.FILL);
                    cellPaint.setAlpha(128);
                    canvas.drawRect(left, top, left + cellSize, top + cellSize, cellPaint);
                }
            }
        }
    }

    private void drawNumbers(Canvas canvas) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].number > 0) {
                    float x = j * cellSize + cellSize / 2;
                    float y = i * cellSize + cellSize / 2 + textPaint.getTextSize() / 3;
                    canvas.drawText(String.valueOf(grid[i][j].number), x, y, textPaint);
                }
            }
        }
    }

    private void drawCompletedPaths(Canvas canvas) {
        for (List<Point> path : completedPaths) {
            drawPath(canvas, path, Color.GREEN);
        }
    }

    private void drawCurrentPath(Canvas canvas) {
        if (!currentPath.isEmpty()) {
            drawPath(canvas, currentPath, Color.BLUE);
        }
    }

    private void drawPath(Canvas canvas, List<Point> path, int color) {
        linePaint.setColor(color);
        for (int i = 0; i < path.size() - 1; i++) {
            Point start = path.get(i);
            Point end = path.get(i + 1);
            canvas.drawLine(
                    start.x * cellSize + cellSize / 2,
                    start.y * cellSize + cellSize / 2,
                    end.x * cellSize + cellSize / 2,
                    end.y * cellSize + cellSize / 2,
                    linePaint
            );
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);

        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleTouchDown(row, col);
                break;
            case MotionEvent.ACTION_MOVE:
                handleTouchMove(row, col);
                break;
            case MotionEvent.ACTION_UP:
                handleTouchUp();
                break;
        }

        invalidate();
        return true;
    }

    private void handleTouchDown(int row, int col) {
        Cell cell = grid[row][col];
        if (cell.number > 0 && !cell.isConnected) {
            selectedCell = cell;
            cell.isSelected = true;
            currentPath.clear();
            currentPath.add(new Point(col, row));
        }
    }

    private void handleTouchMove(int row, int col) {
        if (selectedCell != null) {
            Point last = currentPath.get(currentPath.size() - 1);
            if (isValidMove(last.y, last.x, row, col)) {
                Point current = new Point(col, row);
                if (!currentPath.contains(current)) {
                    currentPath.add(current);
                }
            }
        }
    }

    private void handleTouchUp() {
        if (selectedCell != null && currentPath.size() > 1) {
            Point end = currentPath.get(currentPath.size() - 1);
            Cell endCell = grid[end.y][end.x];

            if (endCell.number == selectedCell.number && !endCell.isConnected) {
                // 连接成功
                completedPaths.add(new ArrayList<>(currentPath));
                selectedCell.isConnected = true;
                endCell.isConnected = true;
                checkGameComplete();
            }
        }

        // 重置状态
        if (selectedCell != null) {
            selectedCell.isSelected = false;
            selectedCell = null;
        }
        currentPath.clear();
    }

    private boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
        return Math.abs(startRow - endRow) + Math.abs(startCol - endCol) == 1;
    }

    private void checkGameComplete() {
        boolean complete = true;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].number > 0 && !grid[i][j].isConnected) {
                    complete = false;
                    break;
                }
            }
        }

        if (complete && listener != null) {
            listener.onGameComplete();
        }
    }

    public interface OnGameCompleteListener {
        void onGameComplete();
    }

    public void setOnGameCompleteListener(OnGameCompleteListener listener) {
        this.listener = listener;
    }

    public void reset() {
        selectedCell = null;
        currentPath.clear();
        completedPaths.clear();
        initializeGrid();
        invalidate();
    }
}