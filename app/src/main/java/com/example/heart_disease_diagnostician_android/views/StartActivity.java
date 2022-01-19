package com.example.heart_disease_diagnostician_android.views;

//这里是基础类

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.heart_disease_diagnostician_android.R;



/*
 * 程序起始页面(终版)：
 * 1.判断是否存在程序运行目录及软件基本数据库，否则创建
 * 2.请求所需的权限
 * 3.读取用户名和密码，与服务器交流判断能否登陆
 * 4.与服务器同步所需信息
 * */


public class StartActivity extends AppCompatActivity {
    //图片生成参数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        Intent to_main_intent = new Intent(this, MainActivity.class);//跳转至设置界面
        startActivity(to_main_intent);
        this.finish();
    }
}