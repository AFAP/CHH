package com.afap.discuz.chh.activity;


import android.os.Bundle;
import android.view.View;

import com.afap.discuz.chh.R;
import com.afap.utils.ToastUtil;

import java.io.File;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        setToolbarTitle(R.string.settings);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear_cache:
                // TODO
                File file = getCacheDir();
                ToastUtil.showShort("已经清除缓存：" + file.length() + "KB");
                break;
        }
    }
}
