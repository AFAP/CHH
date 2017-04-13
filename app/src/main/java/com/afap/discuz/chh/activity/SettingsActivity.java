package com.afap.discuz.chh.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.utils.FileUtil;
import com.afap.utils.ToastUtil;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.io.File;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbarTitle(R.string.settings);

        btn_cache = (Button) findViewById(R.id.btn_clear_cache);

        showCache();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear_cache:
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.clearDiskCaches();

                ToastUtil.showShort("已经清除缓存");
                showCache();
                break;
        }
    }

    private void showCache() {
        long cacheSize = Fresco.getImagePipelineFactory().getMainFileCache().getSize();
        btn_cache.setText(getString(R.string.clear_cache) + "(" + FileUtil.FormatFileSize(cacheSize) + ")");
    }

}
