package com.example.heart_disease_diagnostician_android.plugins.base;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

import com.example.heart_disease_diagnostician_android.baseClass.BaseInformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageOperator {
    //传入病名和概率画柱状图
    public Bitmap pole_img(List<Map.Entry<String, Float>> list) {
        Bitmap image = get_new_img(2100, 1200);
        int avewidth = 300;
        for (int i = 0; i < 3; i++) {
            int k = 0;
            int h = (int) (1000 - 800 * list.get(i).getValue());
            for (int j = 0; j < avewidth; j++) {
                for (k = 1000; k > h; k--) {
                    image.setPixel(i * 2 * avewidth + avewidth + j, k, Color.rgb(0, 0, 255));
                }
            }
            image = addtext(image, list.get(i).getKey(), i * 2 * avewidth + avewidth-100, 1100, 51, 153, 51, 70);
            image = addtext(image, String.valueOf(list.get(i).getValue()), i * 2 * avewidth + avewidth+50, h - 70, 255, 204, 51, 90);
        }
        Log.d("xl", "---------------------------柱状图绘制完毕----------------------------");
        return image;
    }

    //传入心电数据画心电图
    public Bitmap heart_img(List<Float> heartdata, int fr, int to, int pointwidth, int linewidth) {
        int listnum = to-fr;
        int height = 300;
        Bitmap bitmap = get_new_img(pointwidth* BaseInformation.heart_img_pointsperimage, height);
        Bitmap copy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(copy);  //创建画布
        Paint paint = new Paint();  //画笔
        paint.setStrokeWidth(linewidth);  //设置线宽。单位为像素
        paint.setAntiAlias(true); //抗锯齿
        paint.setColor(Color.RED);  //画笔颜色
        canvas.drawBitmap(bitmap, new Matrix(), paint);  //在画布上画一个和bitmap一模一样的图
        //画
        int p0_x = 0, p1_x;
        int p0_y = (int) (height - ((heartdata.get(fr) + 15e-1) / 30e-1) * height), p1_y;
        for (; fr < to; fr++) {
            p1_x = p0_x + pointwidth;
            p1_y = (int) (height - ((heartdata.get(fr) + 15e-1) / 30e-1) * height);
            canvas.drawLine(p0_x, p0_y, p1_x, p1_y, paint);
            p0_x = p1_x;
            p0_y = p1_y;
        }
        Log.d("xl", "---------------------------心电图绘制完毕----------------------------");
        return copy;
    }


    //传入rr间期数据画rr间期图
    public Bitmap rrimg(HashMap<String, Integer> map, int max, int midx, int midy) {
        Bitmap image = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < 150; i++) {
            for (int j = 0; j < 150; j++) {
                String t = String.valueOf(midx - 75 + i) + ',' + String.valueOf(midy - 75 + j);
                int x = 0;
                if (map.get(t) != null) {
                    x = map.get(t);
                }
                int[] rgb = get_rgb(x, max);
                //Log.d("xl",""+x);
                image = change_pixel(image, i, j, rgb);
            }
        }
        Log.d("xl", "---------------------------间期图绘制完毕----------------------------");
        return image;
    }

    //传入rr间期图，一个减小1的像素点和一个增加1的像素点对原图进行修改
    public Bitmap change_rrimg(Bitmap image, String depoint, String inpoint, int denum, int innum, int max, int midx, int midy) {
        int pointx, pointy;
        pointx = depoint.indexOf(',');
        pointy = Integer.parseInt(depoint.substring(pointx + 1));
        pointx = Integer.parseInt(depoint.substring(0, pointx));
        pointx = pointx + 75 - midx;
        pointy = pointy + 75 - midy;
        int[] rgb = get_rgb(denum - 1, max);
        image = change_pixel(image, pointx, pointy, rgb);
        pointy = Integer.parseInt(inpoint.substring(pointx + 1));
        pointx = Integer.parseInt(inpoint.substring(0, pointx));
        pointx = pointx + 75 - midx;
        pointy = pointy + 75 - midy;
        rgb = get_rgb(innum - 1, max);
        image = change_pixel(image, pointx, pointy, rgb);
        Log.d("xl", "---------------------------间期图修改完毕----------------------------");
        return image;
    }


    protected Bitmap change_pixel(Bitmap image, int x, int y, int[] rgb) {
        for (int k1 = 0; k1 < 6; k1++) {
            for (int k2 = 0; k2 < 6; k2++) {
                image.setPixel(y * 6 + k2, x * 6 + k1, Color.rgb(rgb[0], rgb[1], rgb[2]));
            }
        }
        return image;
    }

    protected int[] get_rgb(int x, int max) {
        x = x * 765 / max;
        x = 255 * 3 - x;
        int r, g, b;
        if (x >= 255 * 2) {
            b = 255;
            g = 255;
            r = x - 255 * 2;
            if (r > 255) r = 255;
        } else if (x >= 255) {
            b = 255;
            g = x - 255;
            r = 0;
        } else {
            b = x;
            g = 0;
            r = 0;
        }
        return new int[]{r, g, b};
    }


    //传入长宽返回空白图片
    protected Bitmap get_new_img(int width, int height) {
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setPixel(i, j, Color.rgb(255, 255, 255));
            }
        }
        return image;
    }

    //为柱状图加入病名text
    protected Bitmap addtext(Bitmap bitmap, String text, int x, int y, int r, int g, int b, int textsize) {
        try {
            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            if (bitmapConfig == null) bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // new antialised Paint
            paint.setColor(Color.rgb(r, g, b));       // text color - #3D3D3D
            paint.setTextSize((int) (textsize));           // text size in pixels
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY); // text shadow

            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            canvas.drawText(text, x, y, paint);
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }

}
