package com.paul.imageclassification.Bean;

import org.litepal.crud.LitePalSupport;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/11/19 19：33
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class KindsBean extends LitePalSupport {
    String kindName;
    int kindNumber;
    String firstPhotoPath;

    public String getFirstPhotoPath() {
        return firstPhotoPath;
    }

    public void setFirstPhotoPath(String firstPhotoPath) {
        this.firstPhotoPath = firstPhotoPath;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public int getKindNumber() {
        return kindNumber;
    }

    public void setKindNumber(int kindNumber) {
        this.kindNumber = kindNumber;
    }
}
