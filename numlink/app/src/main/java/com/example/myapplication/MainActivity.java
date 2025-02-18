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
    private NumberLinkView numberLinkView;
    private TextView levelText;
    private int currentLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberLinkView = findViewById(R.id.numberLinkView);
        levelText = findViewById(R.id.levelText);
        Button resetButton = findViewById(R.id.resetButton);

        numberLinkView.setOnGameCompleteListener(() -> {
            showGameCompleteDialog();
        });

        resetButton.setOnClickListener(v -> {
            numberLinkView.reset();
        });
    }

    private void showGameCompleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("恭喜")
                .setMessage("完成当前关卡！")
                .setPositiveButton("下一关", (dialog, which) -> {
                    currentLevel++;
                    levelText.setText("第" + currentLevel + "关");
                    numberLinkView.reset();
                })
                .setNegativeButton("重玩", (dialog, which) -> {
                    numberLinkView.reset();
                })
                .show();
    }
}