package com.example.heart_disease_diagnostician_android.baseClass;


import android.Manifest;

import java.util.HashMap;

public class BaseInformation {
    //图像参数
    public static int heart_img_pointwidth=1;
    public static int heart_img_linewidth=1;
    public static int heart_img_pointsperimage=1500;

    //权限参数
    public static String[] get_permissions(){
        return new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET};
    }
    //Activity回调标识参数
    public static int file_explor_request_code=1;
    public static int main_diag_request_code=2;
    public static int main_real_request_code=3;
    //缓存目录参数
    public static String diag_cache_path="/diag_img";
    public static String real_cache_path="/real_img";
    public static String heart_image_name="heart.png";
    //疾病信息
    public static int disaster_num=3;
    public static HashMap<String,String> get_easydl_disasters_map(){
        HashMap<String, String>map= new HashMap<>();
        map.put("1","心力衰竭一型");
        map.put("2","心力衰竭二型");
        map.put("3","心力衰竭三型");
        return map;
    }

}
