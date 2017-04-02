package com.afap.discuz.chh.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.net.BaseSubscriber;
import com.afap.discuz.chh.net.Network;
import com.afap.discuz.chh.utils.ProgressUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class LoginActivity extends BaseActivity {


    private EditText mUsernameView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setToolbarTitle(R.string.login);

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
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

