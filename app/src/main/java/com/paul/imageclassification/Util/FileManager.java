package com.paul.imageclassification.Util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    /**
     * 适配安卓Q失败
     * */
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
    /**
     * 将获取的path转成Uri
     * */
    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { path }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 通过uri来加载图片
     * */

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<String> getImageFileList(Context context){
        List<String> imagePaths=new ArrayList<>();
        ContentResolver cResolver = context.getContentResolver();

        final Cursor c = cResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        while (c.moveToNext()) {
            //output the path info of image
            final String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            imagePaths.add(path);
        }

        c.close();
        //Thread.currentThread().interrupt();
        return imagePaths;
    }


}
