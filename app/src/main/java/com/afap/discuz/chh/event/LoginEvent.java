package com.afap.discuz.chh.event;

/**
 * Created by vcc on 2017/3/16.
 * 登录事件通知
 */

public class LoginEvent {
    public static final String ACTION_LOGIN_SUCCESS = "action_login_success"; // 登录成功,刷新用户信息
    public static final String ACTION_LOGIN_FAILD = "action_login_faild"; // 登录失败
    public static final String ACTION_LOGIN_OUT = "action_login_out"; // 登出

    private String action;

    public LoginEvent(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
