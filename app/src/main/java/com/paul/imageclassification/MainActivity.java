package com.paul.imageclassification;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;

import com.paul.imageclassification.Util.ImageUtil;
import com.paul.imageclassification.Util.Logger;
import com.paul.imageclassification.Util.MediaLoader;
import com.paul.imageclassification.tensorLite.Classifier;
import com.paul.imageclassification.tensorLite.ClassifierFloatMobileNet;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private Classifier.Model model = Classifier.Model.FLOAT;
    private Classifier.Device device = Classifier.Device.CPU;
    private int numThreads = 5;
    private Classifier classifier;
    private TextView textView,tv_android;
    private Button btn_open;
    private Button btn_scan;
    private String str="";
    Logger logger=new Logger("Test");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.tv_output);
        tv_android=findViewById(R.id.tv_android_version);
        btn_open=findViewById(R.id.btn_openPicker);
        btn_scan=findViewById(R.id.btn_openScan);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Album.image(MainActivity.this) // Image selection.
                        .multipleChoice()
                        .camera(true)
                        .columnCount(4)
                        .selectCount(20)
                        .onResult(new Action<ArrayList<AlbumFile>>() {
                            @Override
                            public void onAction(@NonNull ArrayList<AlbumFile> result) {
                                for(AlbumFile i:result){
                                    str=str+"ImageFileDir："+i.getPath()+"\n";
                                    //str=str+initEveryThing(ImageUtil.getBitmapFromSrc(i.getPath()))+"\n";
                                    str=str+initEveryThing(ImageUtil.getBitmapByPath(MainActivity.this,i.getPath()));
                                }
                                textView.setText(str);
                            }
                        })
                        .onCancel(new Action<String>() {
                            @Override
                            public void onAction(@NonNull String result) {
                            }
                        })
                        .start();
                /*ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM");*/
                //Toast.makeText(MainActivity.this,"维修中",Toast.LENGTH_SHORT).show();

            }
        });
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ScanningActivity.class);
                startActivity(intent);
            }
        });
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        String android_version="Cur Android SDK："+Build.VERSION.SDK_INT+"\n";
        android_version=android_version+"This application has been adapted for 29 and above"+"\n";
        android_version=android_version+"If you have problems,please contact：zhubaoluo@outlook.com";
        tv_android.setText(android_version);
        tv_android.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm;
                ClipData mClipData;
                cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText("ImageClassification", str);
                cm.setPrimaryClip(mClipData);
                Toast.makeText(MainActivity.this,"Results have been copied to clipboard",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public String initEveryThing(Bitmap bitmap){
        logger.printLog("Start");
        String result="";
        try {
            logger.printLog("Reading local images");
            classifier=new ClassifierFloatMobileNet(this,device,numThreads);
            logger.printLog("Classifier created successfully!");
            List<Classifier.Recognition> results=classifier.recognizeImage(ImageUtil.scaleImage(bitmap,224,224));
            logger.printLog("Identifying");
            if(results.size()==0) {
                textView.setText("There is nothing here");
            }else {
                textView.setText(results.get(0).getTitle());
                for(Classifier.Recognition i:results){
                    result=result+"Result："+i.getTitle()+" Possibility："+i.getConfidence()+"\n";
                }
            }
        } catch (IOException e) {
            logger.printLog("Error:"+e);
            e.printStackTrace();
        }
        return result;
    }
}
