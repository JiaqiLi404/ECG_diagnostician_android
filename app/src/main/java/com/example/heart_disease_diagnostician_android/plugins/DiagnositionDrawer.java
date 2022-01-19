package com.example.heart_disease_diagnostician_android.plugins;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.heart_disease_diagnostician_android.baseClass.BaseInformation;
import com.example.heart_disease_diagnostician_android.plugins.base.EasydlOperator;
import com.example.heart_disease_diagnostician_android.plugins.base.ImageOperator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DiagnositionDrawer {
    static String token = null;
    EasydlOperator easydlOperator = new EasydlOperator();
    ImageOperator imageOperator = new ImageOperator();
    Activity activity;
    HashMap<String,Float> disaster_possibilities;
    HashMap<String, String> easydl_disasters_map;
    List<Map.Entry<String, Float>> result_maplist;
    int pic_num=0;
    boolean got_img=false;

    public DiagnositionDrawer() {
        if (token == null) {
            update_token();
        }
        easydl_disasters_map= BaseInformation.get_easydl_disasters_map();
        disaster_possibilities=new HashMap<>();
        String[] disasternames= easydl_disasters_map.values().toArray(new String[0]);
        for(int i=0;i<BaseInformation.disaster_num;i++){
            disaster_possibilities.put(disasternames[i], (float) 0);
        }
    }

    public void update_token() {
        token = easydlOperator.gettoken(activity);
    }
    public String get_result_string(){
        if(!got_img){get_result_image();}
        Map.Entry<String, Float> possible_disease=result_maplist.get(0);
        if(possible_disease.getValue()>0.8){
            return "您很有可能患有"+possible_disease.getKey();
        }else if(possible_disease.getValue()>0.6){
            return "您可能患有"+possible_disease.getKey();
        }else if(possible_disease.getValue()>0.4){
            return "您具有"+possible_disease.getKey()+"的特征";
        }else{
            return "您非常健康";
        }
    }
    public Bitmap get_result_image(){
        got_img=true;
        //从大到小排序
        result_maplist = new ArrayList<>(disaster_possibilities.entrySet());
        result_maplist.sort(new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (Map.Entry<String, Float> e:result_maplist){
            float f=e.getValue()/pic_num;
            if (pic_num==0){f=0;}
            BigDecimal b = new BigDecimal(f);
            f = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            e.setValue(f);
        }
        return imageOperator.pole_img(result_maplist);
    }
    public Bitmap diagnose(Activity activity, String filepath) throws JSONException {
        String result = easydlOperator.get_possibility(activity, filepath, token);
        //解析返回值
        JSONObject toJsonObj = new JSONObject(result);
        try {
            JSONArray toJsonArr = toJsonObj.getJSONArray("results");
            //获取每个病的概率
            HashMap<String, Float> disaster_possibilities_temp = new HashMap<String, Float>();
            for (int i = 0; i < BaseInformation.disaster_num; i++) {
                toJsonObj = toJsonArr.getJSONObject(i);
                String disastername = easydl_disasters_map.get(toJsonObj.getString("name"));
                double dposs = toJsonObj.getDouble("score");
                BigDecimal b = new BigDecimal(dposs);
                float fposs = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                disaster_possibilities_temp.put(disastername, fposs);
                fposs+=disaster_possibilities.get(disastername);
                disaster_possibilities.replace(disastername,fposs);
            }
            //从大到小排序
            List<Map.Entry<String, Float>> list = new ArrayList<>(disaster_possibilities_temp.entrySet());
            list.sort(new Comparator<Map.Entry<String, Float>>() {
                @Override
                public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            pic_num+=1;
            Log.d("sb", "----------------------解析result成功" + toJsonArr.toString());
            return imageOperator.pole_img(list);
        } catch (JSONException e) {
            //存在错误值
            e.printStackTrace();
            String s = (String) toJsonObj.get("error_msg");
            Log.d("sb", "----------------------Easydl返回错误值" + s);
            return null;
        }

    }

}
