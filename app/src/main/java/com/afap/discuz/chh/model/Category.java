package com.afap.discuz.chh.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {
    public static final int TYPE_ARTICLE = 1;
    public static final int TYPE_THREAD = 2;
    public static final int TYPE_FORUM = 3;


    private String id;
    private String name;
    private int type = 1;
    private boolean isLabel = false;
    private List<Category> childrens = new ArrayList<>();
    private boolean isSelected = false;
    private String num;


    public Category(String id, String name, int type, boolean isLabel) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isLabel = isLabel;
    }

    public boolean isLabel() {
        return isLabel;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Category> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<Category> childrens) {
        this.childrens = childrens;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", childrens=" + childrens +
                '}';
    }
}
