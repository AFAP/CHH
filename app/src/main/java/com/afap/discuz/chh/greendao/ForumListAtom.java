package com.afap.discuz.chh.greendao;

import com.afap.discuz.chh.Constant;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ForumListAtom extends BaseListAtom implements Serializable {

    private String cat;
    private String belong;
    private String author_name;
    private String author_id;
    private String time;
    private String num_view;
    private String num_comment;

    public ForumListAtom(String cat, String href, String title, String belong, String author_name, String author_id,
                         String time, String num_view, String num_comment) {
        this.cat = cat;
        this.href = href;
        this.title = title;
        this.belong = belong;
        this.author_name = author_name;
        this.author_id = author_id;
        this.time = time;
        this.num_view = num_view;
        this.num_comment = num_comment;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNum_view() {
        return num_view;
    }

    public void setNum_view(String num_view) {
        this.num_view = num_view;
    }

    public String getNum_comment() {
        return num_comment;
    }

    public void setNum_comment(String num_comment) {
        this.num_comment = num_comment;
    }

    public String getAvatarUrl() {

        String formatId = "" + author_id;
        if (formatId.length() % 2 != 0) {
            formatId = "0" + formatId;
        }

        String url;
        url = Constant.HOST_APP + "/uc_server/data/avatar/000";
        for (int i = 0; i < formatId.length(); i++) {
            if (i % 2 == 0) {
                url = url + "/";
            }
            url = url + formatId.charAt(i);
        }
        url = url + "_avatar_small.jpg";

        return url;

    }


    @Override
    public String toString() {
        return "ForumListAtom{" +
                "cat='" + cat + '\'' +
                ", href='" + href + '\'' +
                ", title='" + title + '\'' +
                ", belong='" + belong + '\'' +
                ", author_name='" + author_name + '\'' +
                ", author_id='" + author_id + '\'' +
                ", time='" + time + '\'' +
                ", num_view='" + num_view + '\'' +
                ", num_comment='" + num_comment + '\'' +
                '}';
    }

    public static List<ForumListAtom> parseFromDocument(Document doc, String cat) {
        List<ForumListAtom> list = new ArrayList<>();

        Elements tbodys = doc.getElementsByAttributeValueContaining("id", "normalthread_");

        for (int i = 0; i < tbodys.size(); i++) {
            Element tr = tbodys.get(i).child(0);
            Element th_2 = tr.child(1);
            Element td_3 = tr.child(2);
            Element td_4 = tr.child(3);

            // 这个暂时不显示，且有的没有分组会异常
            // String belong = th_2.getElementsByTag("em").get(0).text();
            String title = th_2.getElementsByAttributeValue("class", "s xst").get(0).text();
            String href = th_2.getElementsByAttributeValue("class", "s xst").get(0).attr("href");

            String author_name = td_3.getElementsByTag("a").get(0).text();
            String author_id = td_3.getElementsByTag("a").get(0).attr("href");
            author_id = author_id.replaceAll("space-uid-", "").replaceAll(".html", "");
            String time = td_3.getElementsByTag("em").get(0).text();

            String num_comment = td_4.getElementsByTag("a").get(0).text();
            String num_view = td_4.getElementsByTag("em").get(0).text();

            ForumListAtom atom = new ForumListAtom(cat, href, title, "", author_name, author_id, time, num_comment,
                    num_view);
            list.add(atom);
        }
        return list;
    }
}