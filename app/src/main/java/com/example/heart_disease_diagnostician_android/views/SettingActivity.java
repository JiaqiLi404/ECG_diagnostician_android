package com.example.heart_disease_diagnostician_android.views;

//这里是基础类
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.heart_disease_diagnostician_android.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_page);
    }
}