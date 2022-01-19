package com.example.heart_disease_diagnostician_android.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.heart_disease_diagnostician_android.R;
import com.example.heart_disease_diagnostician_android.baseClass.BaseInformation;

/*
 * 程序主界面：
 * 1.主页：包括依据txt和图片的诊断功能和历史记录
 * 2.发现：不同病情的心电图展示
 * 3.消息：平台推送消息或者医生发来的消息或者实时监控的报警信息
 * 4.我的：针对某一病例的诊断记录的病情变化，寻求专业医师的诊断等扩展功能
 * */


public class MainActivity extends AppCompatActivity {
    int diag_filenum = 0, real_filenum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
    }


    //病情诊断按钮按下后执行
    public void to_diag(View view) throws InterruptedException {
        //跳转至诊断界面
        Intent local_intent = new Intent(this, DiagnositionActivity.class);
        local_intent.putExtra("filenum", diag_filenum);
        startActivityForResult(local_intent, BaseInformation.main_diag_request_code);
    }

    //实时诊断按钮按下后执行
    public void to_realtime_diag(View view) throws InterruptedException {
        //跳转至实时诊断界面
        Intent local_intent = new Intent(this, Diagnosition_Realtime_Activity.class);
        local_intent.putExtra("filenum", real_filenum);
        startActivityForResult(local_intent, BaseInformation.main_real_request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BaseInformation.main_diag_request_code) {
                Intent intent = getIntent();
                diag_filenum = intent.getIntExtra("filenum", 0);
            } else if (requestCode == BaseInformation.main_real_request_code) {
                Intent intent = getIntent();
                real_filenum = intent.getIntExtra("filenum", 0);
            }
        }
    }
}