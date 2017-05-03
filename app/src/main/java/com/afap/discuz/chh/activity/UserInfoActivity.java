package com.afap.discuz.chh.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.afap.discuz.chh.App;
import com.afap.discuz.chh.R;
import com.afap.discuz.chh.model.User;
import com.afap.discuz.chh.net.BaseSubscriber;
import com.afap.discuz.chh.net.Network;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.bugly.crashreport.BuglyLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class UserInfoActivity extends BaseActivity implements OnClickListener {


    private User mUser;

    private SimpleDraweeView mAvatarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        setToolbarTitle(R.string.title_user_info);

        mUser = getApp().getUser();

        mAvatarView = (SimpleDraweeView) findViewById(R.id.avatar);
        mAvatarView.setImageURI(mUser.getAvatarUrl());

        TextView tv_username = (TextView) findViewById(R.id.userinfo_username);
        tv_username.setText(mUser.getUser_name());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                break;
            case R.id.img_code:
                break;
        }
    }


    /**
     * 获取初始化信息，验证码什么的
     */
    private void init() {
        Network
                .getAPIService()
                .getLoginInitInfo()
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        Document doc = Jsoup.parse(s);

                        formhash = doc.getElementsByAttributeValue("name", "formhash").get(0).attr("value");
                        BuglyLog.w("formhash------->", formhash);

                        String funstr = "";
                        Elements scripts = doc.getElementsByAttributeValue("reload", "1");
                        for (int i = 0; i < scripts.size(); i++) {
                            Element e = scripts.get(i);
                            if (TextUtils.equals("script", e.tagName()) && e.html().contains("updateseccode")) {
                                funstr = e.html();
                                break;
                            }
                        }
                        if (!TextUtils.isEmpty(funstr)) {
                            seccodehash = funstr.split("'")[1];
                            BuglyLog.w("------->", seccodehash);
                            return Network
                                    .getAPIService()
                                    .getCodeInfo(seccodehash, Math.random());
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        Pattern p = Pattern.compile("src=\"misc.php(.*?)\"");
                        Matcher m = p.matcher(s);
                        ArrayList<String> urls = new ArrayList<>();
                        while (m.find()) {
                            urls.add(m.group(1));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        // 后台刷新，错误不处理
                    }
                });
    }


    private String formhash = "";
    private String seccodehash = "";

    /**
     * 登录成功后重新刷新主页，获取用户ID和名称
     */
    private void getInitInfo() {
        Network
                .getAPIService()
                .getForumMain()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        Document doc = Jsoup.parse(s);
                        Elements as = doc.getElementsByAttributeValue("title", "访问我的空间");
                        if (as != null && as.size() > 0) {
                            Element a = as.get(0);
                            String name = a.text();
                            String id = a.attr("href");
                            id = id.replaceAll("space-uid-", "").replaceAll(".html", "");
                            User user = new User();
                            user.setUser_id(id);
                            user.setUser_name(name);

                            App.getInstance().saveUser(user);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

}

