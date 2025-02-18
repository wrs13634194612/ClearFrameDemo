package com.example.myapplication;



        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private FindDifferenceView findDifferenceView;
    private TextView progressText;
    private Button nextButton;
    private int currentLevel = 0;

    // 关卡数据
    private String[][] levels = {
            {
                    "今天是个好日子，阳光明媚，万里无云。",
                    "今天是个坏日子，阳光明媚，万里无云。"
            },
            {
                    "小明在公园里散步，看见一只可爱的小狗。",
                    "小明在公园里散步，看见一只可爱的小猫。"
            },
            {
                    "如果在某个具体实现上需要更深入的帮助。",
                    "如果在某个具体实现上需要更深入的帮忙。"
            },
            {
                    "根据框架逐步添加细化功能和动画效果。",
                    "根据框架逐渐添加细化功能和动漫效果。"
            },
            {
                    "这个故事讲述了一个勇敢的小男孩。",
                    "这个故事讲述了一个胆小的小男孩。"
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findDifferenceView = findViewById(R.id.findDifferenceView);
        progressText = findViewById(R.id.progressText);
        nextButton = findViewById(R.id.nextButton);

        findDifferenceView.setOnDifferenceFoundListener(new FindDifferenceView.OnDifferenceFoundListener() {
            @Override
            public void onDifferenceFound(int total, int found) {
                progressText.setText(String.format("找到: %d/%d", found, total));
            }

            @Override
            public void onAllDifferencesFound() {
                nextButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "恭喜通关！", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(v -> {
            currentLevel = (currentLevel + 1) % levels.length;
            loadLevel(currentLevel);
        });

        loadLevel(currentLevel);
    }

    private void loadLevel(int level) {
        findDifferenceView.setTexts(levels[level][0], levels[level][1]);
        findDifferenceView.reset();
        nextButton.setEnabled(false);
        progressText.setText("找到: 0/0");
    }
}
