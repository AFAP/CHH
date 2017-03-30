package com.afap.discuz.chh.greendao;

import com.afap.discuz.chh.Constant;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类列表每行元素
 */
public class CategoryListAtom extends BaseListAtom implements Serializable {

    private String cat;
    private String content;
    private String time;
    private String thumb_url;

    public CategoryListAtom() {
    }

    public CategoryListAtom(String cat, String href, String title, String content, String time, String thumb_url) {
        this.cat = cat;
        this.href = href;
        this.title = title;
        this.content = content;
        this.time = time;
        this.thumb_url = thumb_url;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
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

    public static List<CategoryListAtom> parseFromDocument(Document doc, String cat) {
        List<CategoryListAtom> list = new ArrayList<>();

        Element div_ct = doc.getElementById("ct");
        Element div_list = div_ct.getElementsByAttributeValue("class", "bm_c xld").get(0);

        Elements dls = div_list.children();

        for (int i = 0; i < dls.size(); i++) {
            Element dl = dls.get(i);


            String href = dl.child(0).getElementsByTag("a").get(0).attr("href");
            String title = dl.child(0).text();
            String desc = dl.child(1).text();
            String time = dl.child(2).text();

            String img_url = null;

            Elements imgs = dl.child(1).getElementsByTag("img");
            if (imgs.size() > 0) {
                img_url = Constant.HOST_APP + imgs.get(0).attr("src");
            }

            CategoryListAtom atom = new CategoryListAtom(cat, href, title, desc, time, img_url);
            list.add(atom);
        }
        return list;
    }
}