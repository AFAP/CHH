package com.afap.discuz.chh.greendao;


import java.io.Serializable;

public class BaseListAtom implements Serializable {
    protected String href;
    protected String title;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
