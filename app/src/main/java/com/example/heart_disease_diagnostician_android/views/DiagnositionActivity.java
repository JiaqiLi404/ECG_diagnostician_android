package com.example.heart_disease_diagnostician_android.views;

//这里是基础类

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.heart_disease_diagnostician_android.R;
import com.example.heart_disease_diagnostician_android.baseClass.BaseInformation;
import com.example.heart_disease_diagnostician_android.plugins.DiagnositionDrawer;
import com.example.heart_disease_diagnostician_android.plugins.HeartDrawer;
import com.example.heart_disease_diagnostician_android.plugins.PermissionOperator;
import com.example.heart_disease_diagnostician_android.plugins.RRDrawer;
import com.example.heart_disease_diagnostician_android.plugins.base.FileOperator;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;


public class DiagnositionActivity extends AppCompatActivity {
    FileOperator fileOperator = new FileOperator();
    Uri fileuri;
    Bitmap rrimage, diagimage;
    Handler mHandler;
    ImageView rriv;
    ImageView diagiv;
    TextView resulttv;
    Button show_heartimg_buttom;
    int filenum;
    int rrimagenum;
    int max_imagenum;
    String resultString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //显示界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnosition_page);
        //绑定控件
        rriv = findViewById(R.id.imageView_diag_rrimg2);
        diagiv = findViewById(R.id.imageView_diag_diagimg2);
        resulttv = findViewById(R.id.textView_diag_result2);
        show_heartimg_buttom = findViewById(R.id.button_diag_openheartimg);
        //获取权限
        PermissionOperator permissionOperator = new PermissionOperator();
        permissionOperator.initPermission(this);
        //取出filenum数据
        Intent intent = getIntent();
        filenum = intent.getIntExtra("filenum", 0);
        //设置回传filenum数据
        intent = new Intent();
        intent.putExtra("filenum", filenum);
        setResult(RESULT_OK, intent);
        //更新界面处理者
        mHandler = new Handler(msg -> {
            switch (msg.what) {
                case 0:
                    rriv.setImageDrawable(new BitmapDrawable(getResources(), rrimage));
                    rriv.invalidate();
                    diagiv.setImageDrawable(new BitmapDrawable(getResources(), diagimage));
                    diagiv.invalidate();
                    int p = (rrimagenum) * 100 / max_imagenum;
                    StringBuilder sb = new StringBuilder();
                    sb.append("诊断中").append(p).append("%").append('\n');
                    int maxi = (int) (p / 5);
                    for (int i = 0; i <= 20; i++) {
                        if (i > maxi) {
                            sb.append("-");
                        } else {
                            sb.append('>');
                        }
                    }
                    resulttv.setText(sb.toString());
                    break;
                case 1:
                    if (diagimage != null) {
                        diagiv.setImageDrawable(new BitmapDrawable(getResources(), diagimage));
                        resultString = "诊断完毕," + resultString;
                        resulttv.setText(resultString);
                        //show_heartimg_buttom.setVisibility(View.VISIBLE);
                    } else {
                        resultString = "Easydl连接失败,请检查网络";
                        resulttv.setText(resultString);
                        //show_heartimg_buttom.setVisibility(View.VISIBLE);
                    }
                    diagiv.invalidate();
                    resulttv.invalidate();
                    show_heartimg_buttom.invalidate();
                    Toast.makeText(this, "诊断完成", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        });
    }

    //诊断数据文件按钮按下后执行
    public void on_diag_button_click(View view) throws InterruptedException {
        //获取数据文件路径
        fileOperator.choose_and_loadtxt(this);
        //收到路径后，程序在重写方法onActivityResult中继续
    }

    //打开心电图按钮按下后执行
    public void show_heartimg_button_click(View view) throws InterruptedException {
        Uri uri = fileOperator.toUri(this, fileOperator.get_diag_cache_path(this) + "/" + (filenum - 1), BaseInformation.heart_image_name);
        fileOperator.open_local_img(this, uri);
    }

    //收到系统文件管理器返回的路径
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fileuri = fileOperator.activity_result(this, resultCode, data);
        //Toast.makeText(this,filepath,Toast.LENGTH_LONG).show();
        if (resultCode == RESULT_OK) {
            show_heartimg_buttom.setVisibility(View.INVISIBLE);
            //run_on_thread(this);
            run_on_thread(this);
            Toast.makeText(this, "开始诊断", Toast.LENGTH_SHORT).show();
        }
    }


    protected void diag_control(Activity activity) {
        List<Float> heart_list;
        List<String> rr_list;
        int dt = 3500;
        try {
            //heart_list = fileOperator.load_heartdata(activity, fileuri);
            //HeartDrawer heartdrawer = new HeartDrawer(heart_list);
            rr_list = fileOperator.load_rrdata(activity, fileuri);
            //rr_list=heartdrawer.get_RR_data();
            RRDrawer rrdrawer = new RRDrawer(rr_list);
            DiagnositionDrawer diagnositionDrawer = new DiagnositionDrawer();
            max_imagenum = rr_list.size() / dt;
            for (rrimagenum = 0; rrimagenum < max_imagenum; rrimagenum++) {
                rrimage = rrdrawer.draw_rr_img(rrimagenum * dt, (rrimagenum + 1) * dt);
                rrdrawer.save_rr_img(activity, rrimage, filenum, rrimagenum);
                diagimage = diagnositionDrawer.diagnose(this, fileOperator.get_diag_cache_file_path(this, filenum, rrimagenum));
                Message message = new Message();
                message.what = 0;
                if (rrimagenum != max_imagenum - 1)
                    mHandler.sendMessage(message);
            }
            diagimage = diagnositionDrawer.get_result_image();
            resultString = diagnositionDrawer.get_result_string();
            //Bitmap heartimg=heartdrawer.draw_part_heart_img(0);
            //heartdrawer.save_heart_img(this,heartimg,filenum);
            filenum += 1;
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        } catch (IOException | JSONException e) {
            //Toast.makeText(activity, "请检查网络或权限", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    protected void run_on_thread(Activity activity) {
        Runnable r = () -> diag_control(activity);
        Thread t = new Thread(r);
        t.start();
    }
}