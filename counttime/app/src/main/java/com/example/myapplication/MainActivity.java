package com.example.myapplication;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 点击按钮显示DialogFragment
        findViewById(R.id.show_dialog_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountdownDialogFragment dialogFragment = new CountdownDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "CountdownDialogFragment");
            }
        });
    }
}