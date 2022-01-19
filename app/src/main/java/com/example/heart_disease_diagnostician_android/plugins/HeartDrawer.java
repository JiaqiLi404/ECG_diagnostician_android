package com.example.heart_disease_diagnostician_android.plugins;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.heart_disease_diagnostician_android.baseClass.BaseInformation;
import com.example.heart_disease_diagnostician_android.plugins.base.FileOperator;
import com.example.heart_disease_diagnostician_android.plugins.base.ImageOperator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class HeartDrawer {
    ImageOperator imageOperator = new ImageOperator();
    FileOperator fileOperator=new FileOperator();
    List<Float> heartlist;

    public HeartDrawer(List<Float> heartdatalist) {
        heartlist = heartdatalist;
    }

    public Bitmap draw_part_heart_img(int time) {
        //每张心电图包含120个点
        Log.d("xl", "！！！！！！！！！！！！！！！！！心电图绘制完毕！！！！！！！！！！！！！！！！！");
        return imageOperator.heart_img(heartlist, time,time+1000, BaseInformation.heart_img_pointwidth, BaseInformation.heart_img_linewidth);
    }

    public Bitmap draw_whole_heart_img() {
        Log.d("xl", "！！！！！！！！！！！！！！！！！心电图绘制完毕！！！！！！！！！！！！！！！！！");
        return imageOperator.heart_img(heartlist, 0,heartlist.size(), BaseInformation.heart_img_pointwidth, BaseInformation.heart_img_linewidth);
    }
    public boolean save_heart_img(Activity activity, Bitmap image, int filenum) {
        try {
            boolean result = fileOperator.save_img(activity,
                    fileOperator.toUri(activity, fileOperator.get_diag_cache_path(activity) + "/" + filenum+'/',BaseInformation.heart_image_name),
                    image);
            return true;
        }catch (FileNotFoundException e) {
            Toast.makeText(activity,"请检查权限",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.d("xl", "---------------------------文件保存失败----------------------------");
            return false;
        }
    }
    public List<String> get_RR_data(){
        List<String> rr_data=new ArrayList();
        return rr_data;
    }
}
