package com.example.heart_disease_diagnostician_android.plugins;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.heart_disease_diagnostician_android.plugins.base.FileOperator;
import com.example.heart_disease_diagnostician_android.plugins.base.ImageOperator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class RRDrawer {
    ImageOperator imageOperator = new ImageOperator();
    FileOperator fileOperator = new FileOperator();
    List<String> rrlist;
    HashMap<String, Integer> rrmap = null;
    int lastst = 0, lasten = 0;
    int lastmax, lastmidx, lastmidy;
    Bitmap lastimg = null;

    public RRDrawer(List<String> rrdatalist) {
        rrlist = rrdatalist;
    }

    protected HashMap<String, Integer> count_point_num(int rrst, int rren) {
        //统计频数
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = rrst; i < rren; i++) {
            String t = rrlist.get(i);
            if (map.get(t) == null) {
                map.put(t, 1);
            } else {
                Integer integer = map.get(t);
                integer += 1;
                map.replace(t, integer);
                //Log.d("xl", t+':'+integer);
            }
        }
        return map;
    }

    protected Bitmap draw_new_rr_img(int rrst, int rren, boolean hasinf) {
        //获取各点的坐标平均值获得图像中点坐标
        if (!hasinf) {
            rrmap = count_point_num(rrst, rren);
        }
        Collection<String> cs = rrmap.keySet();
        Object[] obj = cs.toArray();
        int midx = 0, midy = 0;
        for (Object o : obj) {
            String s = o.toString();
            int t = s.indexOf(',');
            midx += Integer.parseInt(s.substring(0, t));
            midy += Integer.parseInt(s.substring(t + 1));
        }
        //获取最大值用于定色
        int length = rrmap.size();
        Collection<Integer> ci = rrmap.values();
        obj = ci.toArray();
        Arrays.sort(obj);
        lastmax = Integer.parseInt(obj[length - 1].toString());
        midx /= length;
        midy /= length;
        Bitmap image = imageOperator.rrimg(rrmap, lastmax, midx, midy);
        lastmidx = midx;
        lastmidy = midy;
        return image;
    }

    public Bitmap draw_rr_img(int rrst, int rren) {
        Bitmap image;
        //如果需要重新画图（首次画图）
        if (lastst != rrst - 1 || lasten != rren - 1) {
            image = draw_new_rr_img(rrst, rren, false);
        } else {
            //重新统计坐标平均值
            boolean redraw = false;
            String ststr = rrlist.get(rrst - 1);
            Integer stinteger = rrmap.get(ststr), eninteger;
            stinteger -= 1;
            if (stinteger == 0) {
                rrmap.remove(ststr);
                redraw = true;
            } else {
                rrmap.replace(ststr, stinteger);
            }
            //重新统计频数最大值
            String enstr = rrlist.get(rren - 1);
            if (rrmap.get(enstr) != null) {
                eninteger = rrmap.get(enstr);
                eninteger += 1;
                rrmap.replace(enstr, eninteger);
            } else {
                eninteger = 1;
                rrmap.put(enstr, eninteger);
            }
            if (eninteger > lastmax) {
                lastmax = eninteger;
                redraw = true;
            }
            //如果最大值或中点发生变化则重新画图
            if (redraw) {
                image = draw_new_rr_img(rrst, rren, true);
            } else {
                image = imageOperator.change_rrimg(lastimg, ststr, enstr, stinteger, eninteger, lastmax, lastmidx, lastmidy);
            }
        }
        lastimg = image;
        lastst = rrst;
        lasten = rren;
        return image;
    }

    public boolean save_rr_img(Activity activity, Bitmap image, int filenum, int imagenum) {
        try {
            boolean result = fileOperator.save_img(activity,
                    fileOperator.toUri(activity, fileOperator.get_diag_cache_path(activity) + "/" + filenum+'/',String.valueOf(imagenum)+".png"),
                    image);
            return true;
        }catch (FileNotFoundException e) {
            Toast.makeText(activity,"请检查权限",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.d("xl", "---------------------------文件保存失败----------------------------");
            return false;
        }
    }

}


