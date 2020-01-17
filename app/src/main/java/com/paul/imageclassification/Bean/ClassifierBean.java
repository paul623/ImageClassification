package com.paul.imageclassification.Bean;

import org.litepal.crud.LitePalSupport;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/11/19 19：29
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class ClassifierBean extends LitePalSupport {
    /*
    1.page_image
    2.power_image
    3.writing_image
    * */
    int kind;
    String path;

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
