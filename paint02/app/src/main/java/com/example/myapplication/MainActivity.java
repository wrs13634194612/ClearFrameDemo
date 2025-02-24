package com.example.myapplication;
// MainActivity.java
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawingView);

        Button btnBlack = findViewById(R.id.btnBlack);
        Button btnRed = findViewById(R.id.btnRed);
        Button btnUndo = findViewById(R.id.btnUndo);
        Button btnClear = findViewById(R.id.btnClear);

        btnBlack.setOnClickListener(v -> drawingView.setColor(Color.BLACK));
        btnRed.setOnClickListener(v -> drawingView.setColor(Color.RED));
        btnUndo.setOnClickListener(v -> drawingView.undo());
        btnClear.setOnClickListener(v -> drawingView.clear());
    }
}
