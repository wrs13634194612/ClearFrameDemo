package com.example.myapplication;




import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class SchulteActivity extends AppCompatActivity implements CountdownDialogFragment.CountdownListener {
    private SchulteGridView schulteGridView;
    //    private TextView statusText;
    private TextView timerText;
    private Handler timerHandler;
    private long startTime;
    private boolean isGameRunning;
    private RelativeLayout rl_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schulte);

        schulteGridView = findViewById(R.id.schulteGridView);
        rl_help = findViewById(R.id.rl_help);
//        statusText = findViewById(R.id.statusText);
        timerText = findViewById(R.id.tv_time);
//        Button resetButton = findViewById(R.id.resetButton);

        timerHandler = new Handler();

        schulteGridView.setOnGameCompleteListener(new SchulteGridView.OnGameCompleteListener() {
            @Override
            public void onGameComplete(float timeUsed) {
                isGameRunning = false;
                showGameCompleteDialog(timeUsed);
            }

            @Override
            public void onWrongNumber() {
                Toast.makeText(SchulteActivity.this,
                        "错误！请点击数字: " + schulteGridView.getCurrentNumber(),
                        Toast.LENGTH_SHORT).show();
            }
        });

  /*      resetButton.setOnClickListener(v ->{
            resetGame();
        });*/



        startGame();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        int age = intent.getIntExtra("key",0);
        Log.e("TAG","onStart:"+age);
        // 创建并显示倒计时Dialog
        CountdownDialogFragment dialogFragment = new CountdownDialogFragment();
        dialogFragment.setCountdownListener(this); // 设置回调监听器
        dialogFragment.show(getFragmentManager(), "CountdownDialog");


        /*用来加载不同的网格布局*/

        if (age==3){
            schulteGridView.setText(3,9,dpToPx(50));
        }else if(age==4){
            schulteGridView.setText(4,16,dpToPx(40));
        }else if(age==5){
            schulteGridView.setText(5,25,dpToPx(25));
        }else if(age==6){
            schulteGridView.setText(6,36,dpToPx(20));
        }else if(age==7){
            schulteGridView.setText(7,49,dpToPx(15));
        }else if(age==8){
            schulteGridView.setText(8,64,dpToPx(12));
        }else if(age==9){
            schulteGridView.setText(9,81,dpToPx(10));
        }else{
            schulteGridView.setText(5,25,dpToPx(25));
        }
        rl_help.setOnClickListener(v ->{
            new AlertDialog.Builder(this)
                    .setTitle("帮助")
                    .setMessage("以最快速度从1选到"+age*age)
                    .setPositiveButton("确定", null)
                    .show();
        });
        resetGame();
    }

    private void startGame() {
        isGameRunning = true;
        startTime = System.currentTimeMillis();
        updateTimer();
    }

    private void updateTimer() {
        if (!isGameRunning) return;
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime) / 1000;
        timerText.setText("计时中: " + elapsedTime + "秒");
        /*   statusText.setText("请按顺序点击数字: " + schulteGridView.getCurrentNumber());*/
        timerHandler.postDelayed(this::updateTimer, 1000);
    }

    private void resetGame() {
        schulteGridView.reset();
        startGame();
    }

    private void showGameCompleteDialog(float timeUsed) {
        /*在这儿给地方做弹窗*/
        new AlertDialog.Builder(this)
                .setTitle("恭喜完成！")
                .setMessage(String.format("用时: %.1f秒", timeUsed))
                .setPositiveButton("再来一局", (dialog, which) -> resetGame())
                .setNegativeButton("退出", (dialog, which) -> finish())
                .show();

    /*    TestingDialogFragment testingDialogFragment = new TestingDialogFragment();
        testingDialogFragment.show(getFragmentManager(), "");
        testingDialogFragment.onSetClickDialogListener(new TestingDialogFragment.SetOnClickDialogListener() {

            @Override
            public void onClickDialogListener(int type, boolean boolClick, String content) {
                Log.e("TAG", "onClickDialogListener： " + type + "\t" + boolClick + "\t" + content);
                *//*接下来 还要点击按钮 返回 和重新开始  然后把计时的数据传递过来  然后还需要在新的测试程序里 把所有代码 全部复制过去*//*
            }
        });*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacksAndMessages(null);
    }

    // dp 转 px 的工具方法
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }



    // 实现回调方法
    @Override
    public void onCountdownFinished() {
        // 倒计时结束后的逻辑  这个地方通知  让控件正常显示
        //Toast.makeText(this, "倒计时结束！", Toast.LENGTH_SHORT).show();
        schulteGridView.setVisibility(TextView.VISIBLE);
    }
}