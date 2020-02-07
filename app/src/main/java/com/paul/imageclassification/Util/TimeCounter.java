package com.paul.imageclassification.Util;

public class TimeCounter {
    public long startMills;
    public long totoalMills;
    public void start(){
        startMills=System.nanoTime();
    }
    public String stop(){
        String aa="";
        totoalMills=System.nanoTime() - startMills;
        long mills=totoalMills/1000000000;
        if(mills>=60){
            long minute=mills/60;
            aa=minute+" minute(s) "+mills%60+" second(s)";
        }else {
            aa=mills+" second(s)";
        }
        return aa;
    }
}
