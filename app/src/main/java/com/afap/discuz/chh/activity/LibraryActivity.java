package com.afap.discuz.chh.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.adapter.LibraryAdapter;
import com.afap.discuz.chh.model.Library;

import java.util.ArrayList;
import java.util.List;

/**
 * 用到的库
 */

public class LibraryActivity extends BaseActivity {

    List<Library> mAdapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        initData();

        ListView mListView = (ListView) findViewById(R.id.listView);
        LibraryAdapter mAdapter = new LibraryAdapter(mAdapterList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mAdapterList.get(position).getUrl()));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        mAdapterList = new ArrayList<>();
        mAdapterList.add(new Library("Fresco", "图片加载库", "https://github.com/facebook/fresco"));
    }


}
