package com.example.myapplication;



        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.Color;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.Message;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.widget.ToggleButton;

        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private DrawingBoardView drawingBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingBoard = findViewById(R.id.drawingBoard);

        // 颜色选择
        findViewById(R.id.colorButton).setOnClickListener(v -> showColorPicker());

        // 画笔宽度
        findViewById(R.id.strokeButton).setOnClickListener(v -> showStrokeWidthDialog());

        // 橡皮擦切换
        ToggleButton eraserToggle = findViewById(R.id.eraserToggle);
        eraserToggle.setOnCheckedChangeListener((buttonView, isChecked) ->
                drawingBoard.setEraserMode(isChecked));

        // 撤销
        findViewById(R.id.undoButton).setOnClickListener(v -> drawingBoard.undo());

        // 重做
        findViewById(R.id.redoButton).setOnClickListener(v -> drawingBoard.redo());

        // 清除
        findViewById(R.id.clearButton).setOnClickListener(v -> showClearDialog());

        // 保存
        findViewById(R.id.saveButton).setOnClickListener(v -> saveDrawing());
    }

    private void showColorPicker() {
        new AlertDialog.Builder(this)
                .setTitle("请选择颜色")
                .setPositiveButton("红色", (dialog, which) ->drawingBoard.setColor(Color.RED))
                .setNegativeButton("黑色", (dialog, which) ->drawingBoard.setColor(Color.BLACK))
                .show();
    }

    private void showStrokeWidthDialog() {
        final String[] widths = {"2", "4", "6", "8", "10", "12", "14", "16", "18", "20"};
        new AlertDialog.Builder(this)
                .setTitle("选择画笔宽度")
                .setItems(widths, (dialog, which) -> {
                    float width = Float.parseFloat(widths[which]);
                    drawingBoard.setStrokeWidth(width);
                })
                .show();
    }

    private void showClearDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清除画板")
                .setMessage("确定要清除所有内容吗？")
                .setPositiveButton("确定", (dialog, which) -> drawingBoard.clear())
                .setNegativeButton("取消", null)
                .show();
    }

    private void saveDrawing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        Bitmap bitmap = drawingBoard.save();
        String fileName = "Drawing_" + System.currentTimeMillis() + ".png";
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Toast.makeText(this, "保存成功：" + file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}