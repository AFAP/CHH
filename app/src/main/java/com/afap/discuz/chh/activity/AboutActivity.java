package com.afap.discuz.chh.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.afap.discuz.chh.R;
import com.afap.utils.ContextUtil;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setToolbarTitle(R.string.title_about);

        TextView tv_versionName = (TextView) findViewById(R.id.versionName);
        String versionNam = ContextUtil.getAppVersionName(this);
        tv_versionName.setText(String.format(getString(R.string.app_version_format), versionNam));
    }

}
