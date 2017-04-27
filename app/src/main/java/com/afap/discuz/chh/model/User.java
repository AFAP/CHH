package com.afap.discuz.chh.model;

import com.afap.discuz.chh.Constant;

import java.io.Serializable;

/**
 * 用户信息
 */
public class User implements Serializable {


    private String user_id;
    private String user_name;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAvatarUrl() {

        String formatId = "" + user_id;
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
        url = url + "_avatar_big.jpg";
        return url;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                '}';
    }
}
