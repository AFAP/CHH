package com.afap.discuz.chh.model;

import android.text.TextUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ArticleComment {
    String name;
    String contentHtml;
    String time;

    public ArticleComment(String name, String contentHtml, String time) {
        this.name = name;
        this.contentHtml = contentHtml;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "ArticleComment{" +
                "name='" + name + '\'' +
                ", contentHtml='" + contentHtml + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public static List<ArticleComment> parseFromElements(Document doc) {
        List<ArticleComment> list = new ArrayList<>();

        Element div_ct = doc.getElementById("ct");
        Element div_list = div_ct.getElementsByAttributeValue("class", "bm_c").get(0);

        Elements dls = div_list.children();

        for (int i = 0; i < dls.size(); i++) {
            Element dl = dls.get(i);
            if (!TextUtils.equals(dl.tagName(), "dl")) {
                continue;
            }

            Element dt = dl.child(0);
            String name = dt.child(1).text();
            String time = dt.child(2).text();
            String conhtml = dl.child(1).html();


            ArticleComment atom = new ArticleComment(name, conhtml, time);
            list.add(atom);
        }
        return list;
    }

}
