package com.paul.imageclassification.Util;

import android.content.Context;
import android.util.Log;

public class Logger {
    String label;

    public Logger(String label) {
        this.label = label;
    }
    public void printLog(String content){
        Log.d(label,content);
    }
    public static void printLog(String tag,String content){
        Log.d(tag,content);
    }
}
