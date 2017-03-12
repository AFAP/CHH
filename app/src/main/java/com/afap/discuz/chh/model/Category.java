package com.afap.discuz.chh.model;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    public static final int TYPE_ARTICLE = 1;
    public static final int TYPE_THREAD = 2;

    String id;
    String name;
    int type = 1;
    List<Category> childrens;

    public Category(String id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
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
