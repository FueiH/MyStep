package com.step.pedometer.mystep;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity{

    private Button buttonStep;
    private Button buttonFall;
    private Button buttonSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStep = (Button) findViewById(R.id.buttonStep);
        buttonSport = (Button) findViewById(R.id.buttonSport);
        buttonFall = (Button) findViewById(R.id.buttonFall);
        buttonStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StepActivity.class);
                startActivity(intent);
            }
        });//进入步数检测页面
        buttonSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SportActivity.class);
                startActivity(intent);
            }
        });//进入运动检测页面
        buttonFall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FallActivity.class);
                startActivity(intent);
            }
        });//进入跌倒检测页面

    }

}

