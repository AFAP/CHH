package com.afap.discuz.chh.greendao;


import java.io.Serializable;

public class BaseListAtom implements Serializable {
    protected String href;
    protected String title;
    private int page_label = -1;

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

    public int getPage_label() {
        return page_label;
    }

    public void setPage_label(int page_label) {
        this.page_label = page_label;
    }
}
