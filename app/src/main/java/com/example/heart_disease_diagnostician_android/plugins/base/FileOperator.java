package com.example.heart_disease_diagnostician_android.plugins.base;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.heart_disease_diagnostician_android.baseClass.BaseInformation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FileOperator {
    //读取txt内容返回String
    public String loadtxt(Activity activity, Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = activity.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            Log.d("xl", "---------------------------TXT文件读取完毕----------------------------");
        }
        return stringBuilder.toString();
    }

    //根据文件路径读取byte[] 数组
    public byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length())) {
                BufferedInputStream in = null;
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                return bos.toByteArray();
            }
        }
    }

    //获取内置缓存目录
    protected String getInterPath(Activity activity) {
        return activity.getExternalFilesDir("Pictures").getAbsolutePath();
    }

    //获取诊断缓存目录
    public String get_diag_cache_path(Activity activity) {
        return getInterPath(activity) + BaseInformation.diag_cache_path;
    }

    //获取实时缓存目录
    public String get_real_cache_path(Activity activity) {
        return getInterPath(activity) + BaseInformation.real_cache_path;
    }

    //获取诊断图片文件路径
    public String get_diag_cache_file_path(Activity activity, int filenum, int imagenum) {
        return getInterPath(activity) + BaseInformation.diag_cache_path + "/" + filenum + '/' + String.valueOf(imagenum) + ".png";
    }

    //获取实时图片文件路径
    public String get_real_cache_file_path(Activity activity, int filenum, int imagenum) {
        return getInterPath(activity) + BaseInformation.real_cache_path + "/" + filenum + '/' + String.valueOf(imagenum) + ".png";
    }

    //保存图片
    public boolean save_img(Activity activity, Uri uri, Bitmap image) throws FileNotFoundException {
        ContentResolver cr = activity.getContentResolver();
        ContentValues values = new ContentValues();
        OutputStream outputStream = cr.openOutputStream(uri);
        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        Log.d("xl", "---------------------------成功保存文件----------------------------" + uri.getPath());
        //long id = ContentUris.parseId(uri);
        //Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
        //        MediaStore.Images.Thumbnails.MINI_KIND, null);
        return true;
    }

    //读取RR间期文件,返回点集
    public List<String> load_rrdata(Activity activity, Uri uri) throws IOException {
        String data = loadtxt(activity, uri);
        //将数据转化为点
        List<String> list = new ArrayList();
        StringBuilder now = new StringBuilder();
        StringBuilder last = new StringBuilder("new");
        for (int i = 0; i < data.length(); i++) {
            char t = data.charAt(i);
            if (t != '\n' && t != '\r') {
                now = now.append(t);
                if (!last.toString().equals("new")) {
                    last = last.append(t);
                }
            } else if (t == '\r') {
                continue;
            } else {
                if (!last.toString().equals("new")) {
                    list.add(last.toString());
                }
                last = now;
                last.append(',');
                now = new StringBuilder();
            }
        }
        Log.d("xl", "---------------------------RR间期文件读取完毕----------------------------");
        return list;
    }

    //读取心电图文件,返回数组
    public List<Float> load_heartdata(Activity activity, Uri uri) throws IOException {
        String data = loadtxt(activity, uri);
        //将数据转化为点
        List<Float> list = new ArrayList();
        StringBuilder now = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            char t = data.charAt(i);
            if (t != '\n' && t != '\r') {
                now = now.append(t);
            } else if (t == '\r') {
                continue;
            } else {
                if (now.length() > 2) {
                    list.add(Float.parseFloat(now.toString()));
                    now = new StringBuilder();
                }
            }
        }
        Log.d("xl", "---------------------------心电图文件读取完毕----------------------------" + String.valueOf(list.size()));
        return list;
    }

    //让用户选择一个TXT文件
    public void choose_and_loadtxt(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            Log.d("xl", "---------------------------调用系统文件管理器----------------------------");
            activity.startActivityForResult(Intent.createChooser(intent, "请选择心电RR间期的数据文件"),
                    BaseInformation.file_explor_request_code);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    //返回打开的路径
    //@Override
    //protected void onActivityResult(int requestCode,int resultCode,Intent data){
    //        super.onActivityResult(requestCode,resultCode,data);
    //}
    public Uri activity_result(Activity activity, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                String t = getPath(activity, uri);
                Log.d("xl", "---------------------------得到文件路径：----------------------------" + t);
                return uri;
            }
        }
        return null;
    }


    //路径转uri
    public Uri toUri(Context context, String filePath, String filename) {
        Log.d("sb", "-----------------------------------" + filePath);
        File f = new File(filePath + '/');
        if (!f.exists())
            if (f.mkdirs()) {
                Log.d("sb", ">>>>>>>>>>>>>>>>>>>>>目录创建成功");
            } else {
                Log.d("sb", "<<<<<<<<<<<<<<<<<<<<<<目录创建失败");
            }
        f = new File(filePath + '/' + filename);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getApplicationInfo().packageName + ".fileprovider", f);
        }
        return Uri.fromFile(f);
    }

    public void open_local_img(Activity activity, Uri uri) {
        Intent intent = new Intent();
        intent.setDataAndType(uri, "image/png");
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivity(intent);
    }

    //获取系统返回的路径
    protected String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("raw".equalsIgnoreCase(type)) {
                    return split[1];
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    protected String getDataColumn(Context context, Uri uri, String selection,
                                   String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    protected boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    protected boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    protected boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
