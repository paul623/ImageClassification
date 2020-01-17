package com.paul.imageclassification.Util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static List<String> getFilePathList(Context context){
        List<String> pathLists=new ArrayList<>();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Thumbnails.DATA
        };
        //全部图片
        String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?";
        //指定格式
        String[] whereArgs = {"image/jpeg", "image/png", "image/jpg"};
        //查询
        Cursor mCursor = context.getContentResolver().query(
                mImageUri, projection, null, null,
                null);

        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                int thumbPathIndex = mCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                int timeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                int pathIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int id = mCursor.getColumnIndex(MediaStore.Images.Media._ID);

                Long date = mCursor.getLong(timeIndex)*1000;
                String filepath,thumbPath;
                //适配Android Q
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.P){
                    thumbPath  =MediaStore.Images.Media
                            .EXTERNAL_CONTENT_URI
                            .buildUpon()
                            .appendPath(String.valueOf(mCursor.getInt(id))).build().toString();
                    filepath = thumbPath;
                    pathLists.add(filepath);
                }else{
                    thumbPath = mCursor.getString(thumbPathIndex);
                    filepath = mCursor.getString(pathIndex);
                    //判断文件是否存在，存在才去加入
                    File file=new File(filepath);
                    if(file.exists()){
                        pathLists.add(filepath);
                    }

                }
            }
            mCursor.close();
        }
        return pathLists;
    }
}
