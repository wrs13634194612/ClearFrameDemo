package com.example.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView btn_three,btn_four,btn_five,btn_six,btn_seven,btn_eight,btn_nine;
    private MusicService musicService;
    private boolean isBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicService.playMusic(); // 在绑定后播放音乐
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_three=findViewById(R.id.btn_three);

        btn_four=findViewById(R.id.btn_four);
        btn_five=findViewById(R.id.btn_five);
        btn_six=findViewById(R.id.btn_six);
        btn_seven=findViewById(R.id.btn_seven);
        btn_eight=findViewById(R.id.btn_eight);
        btn_nine=findViewById(R.id.btn_nine);

        btn_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SchulteActivity.class);
                intent.putExtra("key", 3);
                startActivity(intent);
            }
        });
        btn_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SchulteActivity.class);
                intent.putExtra("key", 4);
                startActivity(intent);
            }
        });
        btn_five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SchulteActivity.class);
                intent.putExtra("key", 5);
                startActivity(intent);
            }
        });
        btn_six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SchulteActivity.class);
                intent.putExtra("key", 6);
                startActivity(intent);
            }
        });
        btn_seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SchulteActivity.class);
                intent.putExtra("key", 7);
                startActivity(intent);
            }
        });
        btn_eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SchulteActivity.class);
                intent.putExtra("key", 8);
                startActivity(intent);
            }
        });
        btn_nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SchulteActivity.class);
                intent.putExtra("key", 9);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 绑定服务
        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 解除绑定
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}