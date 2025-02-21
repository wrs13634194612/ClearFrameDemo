package com.example.myapplication;



import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CountdownDialogFragment extends DialogFragment {

    private TextView countdownText;
    private CountDownTimer countDownTimer;
    // 定义回调接口
    public interface CountdownListener {
        void onCountdownFinished();
    }

    private CountdownListener listener;

    // 设置回调监听器
    public void setCountdownListener(CountdownListener listener) {
        this.listener = listener;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置半透明样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.SemiTransparentDialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 创建Dialog
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false); // 禁止点击外部取消Dialog
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.dialog_countdown, container, false);
        countdownText = view.findViewById(R.id.countdown_text);

        // 启动倒计时
        startCountdown();

        return view;
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 更新倒计时文本
                long secondsRemaining = millisUntilFinished / 1000;
                countdownText.setText(String.valueOf(secondsRemaining+1));
            }

            @Override
            public void onFinish() {
                // 倒计时结束，关闭Dialog
                if (listener != null) {
                    listener.onCountdownFinished(); // 触发回调
                }
                dismiss();
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 取消倒计时，防止内存泄漏
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}