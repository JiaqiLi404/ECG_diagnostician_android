package com.example.heart_disease_diagnostician_android.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heart_disease_diagnostician_android.R;
import com.example.heart_disease_diagnostician_android.plugins.HeartDrawer;
import com.example.heart_disease_diagnostician_android.plugins.base.FileOperator;

import java.util.ArrayList;
import java.util.List;

public class Diagnosition_Realtime_Activity extends AppCompatActivity {
    FileOperator fileOperator = new FileOperator();
    Uri fileuri;
    Handler mHandler;
    ImageView rriv;
    ImageView diagiv;
    ImageView heartiv;
    TextView resulttv;
    TextView timetv;
    Button stenbu;
    Button clearbu;
    boolean isfinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnosition_realtime_page);
        //绑定控件
        timetv = findViewById(R.id.textView_realtime_time);
        diagiv = findViewById(R.id.imageView_realtime_diagimg);
        resulttv = findViewById(R.id.textView_realtime_result);
        rriv = findViewById(R.id.imageView_realtime_rrimg);
        heartiv = findViewById(R.id.imageView_realtime_heartimg);
        stenbu=findViewById(R.id.button_realtime_start);
        clearbu=findViewById(R.id.button_realtime_clear);
        //更新界面处理者
        mHandler = new Handler(msg -> {
            switch (msg.what) {
                //倒计时
                case 0:
                    Bundle bundle=msg.getData();
                    String text=bundle.getString("text");
                    timetv.setText(text);
                    break;
                case 1:
                    break;
            }
            return false;
        });
    }
    //收到系统文件管理器返回的路径
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fileuri = fileOperator.activity_result(this, resultCode, data);
        //Toast.makeText(this,filepath,Toast.LENGTH_LONG).show();
        if (resultCode == RESULT_OK) {
            run_clock_thread();
            run_conntrol_thread();
            Toast.makeText(this, "开始诊断", Toast.LENGTH_SHORT).show();
        }
    }
    //开始按钮按下后执行
    public void on_real_start_button_click(View view) throws InterruptedException {
        if(isfinish){
            isfinish=false;
            //fileOperator.choose_and_loadtxt(this);
            //run_clock_thread();
            stenbu.setText("暂停");
        }else{
            isfinish=true;
            stenbu.setText("开始");
        }
    }
    protected void clock_thread() throws InterruptedException {
        int hour = 0, minute = 0, second = 0;
        String basestr = "累计时间  ";
        while (!isfinish) {
            second += 1;
            if (second == 60) {
                minute += 1;
                second = 0;
                if (minute == 60) {
                    hour += 1;
                    minute = 0;
                }
            }

            String text = basestr;
            if (hour < 10) {
                text += "0";
            }
            text += hour + " : ";
            if (minute < 10) {
                text += "0";
            }
            text += minute + " : ";
            if (second < 10) {
                text += "0";
            }
            text += second;
            Bundle bundle = new Bundle();
            bundle.putString("text", text);
            Message message = new Message();
            message.what = 0;
            message.setData(bundle);
            mHandler.sendMessage(message);
            Thread.sleep(1000);
        }
    }
    protected void control_thread()  {
        int ntime=0;
        List<Float> heartlist=new ArrayList<>();
        List<String> rrlist=new ArrayList<>();
        HeartDrawer heartDrawer=new HeartDrawer(heartlist);


    }
    protected void run_clock_thread() {
        Runnable r = () -> {
            try {
                clock_thread();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    protected void run_conntrol_thread(){
        Runnable r = () -> {
            control_thread();
        };
        Thread t = new Thread(r);
        t.start();
    }
}
