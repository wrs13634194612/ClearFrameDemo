package com.example.myapplication;


        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;


public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题栏
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);
    }
}
