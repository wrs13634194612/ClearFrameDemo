package com.example.myapplication;



        import android.content.Context;
        import android.content.res.Resources;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Paint;
        import android.util.AttributeSet;
        import android.view.MotionEvent;
        import android.view.View;



/**
 * Created by 小天 on 2018/1/12.
 */

public class MainView extends View {

    private Bitmap startBtn;
    private Bitmap background; // 背景图片
    private Paint paint; // 画笔
    private int screenWidth;
    private int screenHeight;
    private int x, y, w, h; // 开始按钮显示的区域
    private MainViewInterface listener; // 事件监听接口

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = this.getResources();
        startBtn = BitmapFactory.decodeResource(res, R.mipmap.start_btn);
        // 加载背景图片
        background = BitmapFactory.decodeResource(res, R.mipmap.play_bg);
        screenHeight = DisplayUtil.getScreenHeight(context);
        screenWidth = DisplayUtil.getScreenWidth(context);
        // 实例化画笔
        paint = new Paint();
        paint.setAntiAlias(false); // 消除锯齿
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bgBitmap, btnBitmap;
        // 绘制背景图片
        bgBitmap = DisplayUtil.resizeBitmap(background, screenWidth, screenHeight);
        canvas.drawBitmap(bgBitmap, 0, 0, paint);
        // 计算开始按钮显示的坐标
        w = (int) (screenWidth * 0.5);
        h = (int) (screenHeight * 0.08);
        x = screenWidth / 2 - w / 2;
        y = screenHeight - (int) (screenHeight * 0.28);
        btnBitmap = DisplayUtil.resizeBitmap(startBtn, w, h);
        canvas.drawBitmap(btnBitmap, x, y, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取当前触控位置
        int ex = (int) event.getX();
        int ey = (int) event.getY();
        switch (event.getAction()) {
            // 按下
            case MotionEvent.ACTION_DOWN:
                if (ex > x && ex < (x + w)
                        && ey > y && ey < (y + h)) {
                    // Toast.makeText(MainView.this.getContext(), "点击开始", Toast.LENGTH_SHORT).show();
                    listener.startGame();
                }
                break;
            // 移动
            case MotionEvent.ACTION_MOVE:
                break;
            // 抬起
            case MotionEvent.ACTION_UP:
                break;
        }
        // 刷新界面
        invalidate();
        // 使系统响应事件，返回true
        return true;
    }

    /**
     * 此方法交由MainActivity调用
     * 目的是获取公共接口对象以实现需要功能
     *
     * @param mvi
     */
    public void setMainViewInterface(MainViewInterface mvi) {
        this.listener = mvi;
    }
}
