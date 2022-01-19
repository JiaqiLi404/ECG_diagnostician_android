package com.example.heart_disease_diagnostician_android.plugins.base;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasydlOperator {
    String clientId = "eUckWhPeTPopFvxIpoPBuwgR";
    String clientSecret = "BwX7x87GgVcQeZuxVZeyOo5Tlsx5RSUu";
    FileOperator fileOperator = new FileOperator();
    Gson gson = new GsonBuilder().create();

    public String gettoken(Activity activity) {
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + clientId
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + clientSecret;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            JSONObject jsonObject = new JSONObject(result.toString());
            String t = jsonObject.getString("access_token");
            Log.d("xl", "---------------------------token获取完毕----------------------------" + t);
            return t;
        } catch (Exception e) {
            Log.d("xl", "---------------------------token获取失败----------------------------");
            Toast.makeText(activity, "请检查网络连接", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public String get_possibility(Activity activity, String filepath, String token) {
        String file = null;
        try {
            file = Base64Util.encode(fileOperator.readFileByBytes(filepath));
            int i = file.indexOf(',');
            file = file.substring(i + 1);
        } catch (IOException e) {
            Toast.makeText(activity, "请检查权限", Toast.LENGTH_LONG).show();
            return null;
        }
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/classification/335_ml";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", file);
            map.put("top_num", "5");

            String param = gson.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。

            String result = HttpUtil.post(url, token, "application/json", param);
            return result;
        } catch (Exception e) {
            Toast.makeText(activity, "请检查网络", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }


}
