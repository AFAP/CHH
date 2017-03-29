package com.afap.discuz.chh.greendao;

import com.afap.discuz.chh.Constant;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CCL on 2017/3/29.
 */

public class ForumListAtom implements Serializable {

    private String cat;
    private String href;
    private String title;
    private String belong;
    private String content;
    private String time;
    private String thumb_url;

    public ForumListAtom() {
    }

    public ForumListAtom(String cat, String href, String title, String belong) {
        this.cat = cat;
        this.href = href;
        this.title = title;
        this.belong = belong;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    @Override
    public String toString() {
        return "CategoryListAtom{" +
                "cat='" + cat + '\'' +
                ", href='" + href + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", thumb_url='" + thumb_url + '\'' +
                '}';
    }

    public static List<ForumListAtom> parseFromDocument(Document doc, String cat) {
        List<ForumListAtom> list = new ArrayList<>();

        Elements tbodys = doc.getElementsByAttributeValueContaining("id", "normalthread_");

        for (int i = 0; i < tbodys.size(); i++) {
            Element tbody = tbodys.get(i);
            Element th_2 = tbody.child(0);


            String belong = th_2.getElementsByTag("em").get(0).text();
            String title = th_2.getElementsByAttributeValue("class", "s xst").get(0).text();


            ForumListAtom atom = new ForumListAtom(cat, "", title, belong);
            list.add(atom);
        }
        return list;
    }
}