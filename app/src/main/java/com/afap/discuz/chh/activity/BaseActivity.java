package com.afap.discuz.chh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afap.discuz.chh.App;
import com.afap.discuz.chh.R;


public class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected void initToolbar() {
        // 设置Toolbar相关参数
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void setToolbarTitle(int id) {
        setToolbarTitle(getString(id));
    }

    protected void setToolbarTitle(String title) {
        if (toolbar == null) {
            initToolbar();
        }
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(title); //设置Toolbar标题
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_base, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
//        else if (item.getItemId() == R.id.action_help) {
//            Intent mIntent = new Intent(this, HelpActivity.class);
//            startActivity(mIntent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    protected App getApp() {
        return App.getInstance();
    }


}
