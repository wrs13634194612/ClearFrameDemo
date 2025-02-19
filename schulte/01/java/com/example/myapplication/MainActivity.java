package com.example.myapplication;



        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {
    private SchulteGridView schulteGridView;
    private TextView statusText;
    private TextView timerText;
    private Handler timerHandler;
    private long startTime;
    private boolean isGameRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schulteGridView = findViewById(R.id.schulteGridView);
        statusText = findViewById(R.id.statusText);
        timerText = findViewById(R.id.timerText);
        Button resetButton = findViewById(R.id.resetButton);

        timerHandler = new Handler();

        schulteGridView.setOnGameCompleteListener(new SchulteGridView.OnGameCompleteListener() {
            @Override
            public void onGameComplete(float timeUsed) {
                isGameRunning = false;
                showGameCompleteDialog(timeUsed);
            }

            @Override
            public void onWrongNumber() {
                Toast.makeText(MainActivity.this,
                        "错误！请点击数字: " + schulteGridView.getCurrentNumber(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> resetGame());

        startGame();
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
        timerText.setText("用时: " + elapsedTime + "秒");
        statusText.setText("请按顺序点击数字: " + schulteGridView.getCurrentNumber());

        timerHandler.postDelayed(this::updateTimer, 1000);
    }

    private void resetGame() {
        schulteGridView.reset();
        startGame();
    }

    private void showGameCompleteDialog(float timeUsed) {
        new AlertDialog.Builder(this)
                .setTitle("恭喜完成！")
                .setMessage(String.format("用时: %.1f秒", timeUsed))
                .setPositiveButton("再来一局", (dialog, which) -> resetGame())
                .setNegativeButton("退出", (dialog, which) -> finish())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacksAndMessages(null);
    }
}