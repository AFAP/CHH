package com.afap.discuz.chh.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.afap.discuz.chh.Constant;
import com.afap.discuz.chh.R;
import com.afap.discuz.chh.net.BaseSubscriber;
import com.afap.discuz.chh.net.Network;
import com.afap.discuz.chh.utils.ProgressUtils;
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
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class LoginActivity extends BaseActivity {


    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mPCodeView;
    private SimpleDraweeView mCodeImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setToolbarTitle(R.string.login);

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPCodeView = (EditText) findViewById(R.id.code);
        mCodeImageView = (SimpleDraweeView) findViewById(R.id.img_code);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        init();
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
                            String hashcode = funstr.split("'")[1];
                            BuglyLog.w("------->", hashcode);
                            return Network
                                    .getAPIService()
                                    .getCodeInfo(hashcode, Math.random());
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
                        ArrayList<String> strs = new ArrayList<>();
                        while (m.find()) {
                            strs.add(m.group(1));
                        }
                        String imgUrl = Constant.HOST_APP + "misc.php" + strs.get(0);
                        BuglyLog.w("------->", imgUrl);
                        mCodeImageView.setImageURI(imgUrl);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        // 后台刷新，错误不处理
                    }
                });
    }


    private void attemptLogin() {
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_username_required));
            mUsernameView.requestFocus();
            return;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_password_required));
            mPasswordView.requestFocus();
            return;
        }


        Network
                .getAPIService()
                .login(username, password)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        ProgressUtils.show(R.string.load_login, LoginActivity.this, false);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        Document doc = Jsoup.parse(s);

                        // TODO: 2017/4/2
                        ProgressUtils.dismiss();
                        mPasswordView.setError(getString(R.string.error_invalid_email_or_password));
                        mPasswordView.requestFocus();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 后台刷新，错误不处理
                        ProgressUtils.dismiss();
                        super.onError(e);
                    }
                });


    }


}

