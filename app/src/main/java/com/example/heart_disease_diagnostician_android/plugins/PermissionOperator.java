package com.example.heart_disease_diagnostician_android.plugins;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.heart_disease_diagnostician_android.baseClass.BaseInformation;

import java.util.ArrayList;
import java.util.List;

public class PermissionOperator {
    String[] permissions = BaseInformation.get_permissions();
    //创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
    List<String> mPermissionList = new ArrayList<>();
    public void initPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        mPermissionList.clear();//清空没有通过的权限
        //逐个判断所有权限是否已经通过
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);//添加还未授予的权限
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            int mRequestCode = 100;
            ActivityCompat.requestPermissions(activity, permissions, mRequestCode);
        }
    }
}
