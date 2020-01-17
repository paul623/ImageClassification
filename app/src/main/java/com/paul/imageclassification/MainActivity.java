package com.paul.imageclassification;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

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
    private TextView textView;
    private Button btn_open;
    private Button btn_scan;
    private String str="";
    Logger logger=new Logger("测试！");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.tv_output);
        btn_open=findViewById(R.id.btn_openPicker);
        btn_scan=findViewById(R.id.btn_openScan);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Album.image(MainActivity.this) // Image selection.
                        .multipleChoice()
                        .camera(true)
                        .columnCount(4)
                        .selectCount(9)
                        .onResult(new Action<ArrayList<AlbumFile>>() {
                            @Override
                            public void onAction(@NonNull ArrayList<AlbumFile> result) {
                                for(AlbumFile i:result){
                                    str=str+"图片路径："+i.getPath()+"\n";
                                    str=str+initEveryThing(ImageUtil.getBitmapFromSrc(i.getPath()))+"\n";
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
    }

    public String initEveryThing(Bitmap bitmap){
        logger.printLog("开始执行");
        String result="";
        try {
            logger.printLog("读取本地图片中");
            classifier=new ClassifierFloatMobileNet(this,device,numThreads);
            logger.printLog("分类器创建成功！");
            List<Classifier.Recognition> results=classifier.recognizeImage(ImageUtil.scaleImage(bitmap,224,224));
            logger.printLog("正在识别");
            if(results.size()==0) {
                textView.setText("啥都没有");
            }else {
                textView.setText(results.get(0).getTitle());
                for(Classifier.Recognition i:results){
                    result=result+"推测结果："+i.getTitle()+" 可能性："+i.getConfidence()+"\n";
                }
            }
        } catch (IOException e) {
            logger.printLog("出错了！"+e);
            e.printStackTrace();
        }
        return result;
    }
}
