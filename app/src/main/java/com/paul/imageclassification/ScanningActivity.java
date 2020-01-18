package com.paul.imageclassification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ListMenuItemView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.paul.imageclassification.Adapter.ScanningListAdapter;
import com.paul.imageclassification.Bean.ClassifierBean;
import com.paul.imageclassification.Bean.KindsBean;
import com.paul.imageclassification.Util.FileManager;
import com.paul.imageclassification.Util.ImageUtil;
import com.paul.imageclassification.Util.Logger;
import com.paul.imageclassification.tensorLite.Classifier;
import com.paul.imageclassification.tensorLite.ClassifierFloatMobileNet;
import com.yanzhenjie.album.Album;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanningActivity extends Activity {
    private TextView textView;
    String src="";
    private Classifier.Model model = Classifier.Model.FLOAT;
    private Classifier.Device device = Classifier.Device.CPU;
    private int numThreads = 1;
    private Classifier classifier;
    Logger logger=new Logger("ScanningActivity");
    public List<String> allPath;
    public ListView listView;
    public double correct_point=0.90;
    public ArrayList<String> page_imagePaths=new ArrayList<>();
    public ArrayList<String> power_imagePaths=new ArrayList<>();
    public ArrayList<String> writing_imagePaths=new ArrayList<>();
    public Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case 1:
                    processPhoto();
                    src=src+"图片处理完成\n";
                    textView.setText(src);
                    break;
                case 2:
                    src=src+"正在执行分类汇总\n";
                    textView.setText(src);
                    listView.setAdapter(new ScanningListAdapter(dealWithResult(),ScanningActivity.this));
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
        LitePal.initialize(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        try {
            classifier=new ClassifierFloatMobileNet(this,device,numThreads);
            listView=findViewById(R.id.lv_album_lessonselect);
            textView=findViewById(R.id.tv_content);
            Album.galleryAlbum(this);
            getAllPhotos();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i){
                        case 0:
                            Album.gallery(ScanningActivity.this)
                                    .checkedList(page_imagePaths)
                                    .checkable(false)
                                    .start();
                            break;
                        case 1:
                            Album.gallery(ScanningActivity.this)
                                    .checkedList(power_imagePaths)
                                    .checkable(false)
                                    .start();
                            break;
                        case 2:
                            Album.gallery(ScanningActivity.this)
                                    .checkedList(writing_imagePaths)
                                    .checkable(false)
                                    .start();
                            break;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void getAllPhotos(){
        //if语句 没有读写SD卡的权限，就申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            //获取所有图片存入list集合 getGalleryPhotos方法
            allPath = new ArrayList<>();
            //allPath = getGalleryPhotos(getContentResolver());

            //allPath= FileManager.getFilePathList(ScanningActivity.this);
            allPath=FileManager.getImageFileList(ScanningActivity.this);
            Log.d("tgw所有图片地址", "initAbbreviation: " + allPath.toString());
            src=src+"获取所有图片地址共"+allPath.size()+"张\n";
            src=src+"目前筛选准确率为"+correct_point+"\n";
            textView.setText(src);
            Message message=new Message();
            src=src+"正在识别图片,请耐心等待\n";
            textView.setText(src);
            message.what=1;
            handler.sendMessage(message);
        }

    }
    private static ArrayList<String> getGalleryPhotos(ContentResolver resolver) {
        ArrayList<String> galleryList = new ArrayList<String>();
        try {
            //获取所在相册和相册id
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            //按照id排序
            final String orderBy = MediaStore.Images.Media._ID;

            //相当于sql语句默认升序排序orderBy，如果降序则最后一位参数是是orderBy+" desc "
            Cursor imagecursor =
                    resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                            null, orderBy);

            //从数据库中取出图存入list集合中
            int cont=0;
            if (imagecursor != null && imagecursor.getCount() > 0) {
                while (imagecursor.moveToNext()&&cont<200) {
                    int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                    Log.d("tgw7", "getGalleryPhotos: " + dataColumnIndex);
                    //String path = "file://" + imagecursor.getString(dataColumnIndex);
                    String path = imagecursor.getString(dataColumnIndex);
                    Log.d("tgw5", "getGalleryPhotos: " + path);
                    galleryList.add(path);
                    cont++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 进行反转集合
        Collections.reverse(galleryList);
        return galleryList;
    }
    public void processPhoto(){
        LitePal.deleteAll(ClassifierBean.class);
        int num=allPath.size();
        if(num>=200){
            num=200;
        }
        for(int i=0;i<num;i++){
            initEveryThing(allPath.get(i),i);
        }
        Message message=new Message();
        message.what=2;
        handler.sendMessage(message);

    }
    public String initEveryThing(String path,int i){
        //Bitmap bitmap=ImageUtil.getBitmapFromSrc(path);
        Bitmap bitmap= ImageUtil.getBitmapByPath(ScanningActivity.this,path);
        logger.printLog("开始执行");
        String result="";
        logger.printLog("读取本地图片中");
        logger.printLog("分类器创建成功！");
        List<Classifier.Recognition> results = classifier.recognizeImage(ImageUtil.scaleImage(bitmap, 224, 224));
        logger.printLog("正在识别");
        if (results.size() == 0) {
        } else {
            if (results.get(0).getConfidence() >= correct_point) {
                ClassifierBean classifierBean = new ClassifierBean();
                switch (results.get(0).getTitle()) {
                    case "page_image":
                        classifierBean.setKind(1);
                        break;
                    case "power_image":
                        classifierBean.setKind(2);
                        break;
                    case "writing_image":
                        classifierBean.setKind(3);
                        break;
                }
                classifierBean.setPath(path);
                classifierBean.save();
            }
        }
        return result;

    }
    public List<KindsBean> dealWithResult(){
        src=src+"正在处理最后结果汇总···\n";
        textView.setText(src);
        int a=0;
        int b=0;
        int c=0;
        List<ClassifierBean> list= LitePal.findAll(ClassifierBean.class);
        KindsBean page_image=new KindsBean();
        KindsBean power_image=new KindsBean();
        KindsBean writing_image=new KindsBean();
        for(ClassifierBean item:list){
            if(item.getKind()==1){
                page_image.setFirstPhotoPath(item.getPath());
                a++;
                page_imagePaths.add(item.getPath());
            }else if(item.getKind()==2){
                power_image.setFirstPhotoPath(item.getPath());
                b++;
                power_imagePaths.add(item.getPath());
            }else {
                writing_image.setFirstPhotoPath(item.getPath());
                c++;
                writing_imagePaths.add(item.getPath());
            }
        }
        page_image.setKindNumber(a);
        page_image.setKindName("文档");
        power_image.setKindNumber(b);
        power_image.setKindName("幻灯片");
        writing_image.setKindNumber(c);
        writing_image.setKindName("黑板");
        List<KindsBean> list1=new ArrayList<>();
        list1.add(page_image);
        list1.add(power_image);
        list1.add(writing_image);
        return list1;
    }
}
