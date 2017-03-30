package com.afap.discuz.chh.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.afap.discuz.chh.adapter.ForumListAdapter;
import com.afap.discuz.chh.greendao.ForumListAtom;
import com.afap.discuz.chh.model.Category;
import com.afap.discuz.chh.net.Network;
import com.tencent.bugly.crashreport.BuglyLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ForumListFragment extends BaseListFragment {
    private final static int PAGE_SIZE = 15;

    private List<ForumListAtom> mAdapterList;
    private ForumListAdapter mAdapter;

    public static ForumListFragment newInstance(Category category) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_CATEGORY, category);
        ForumListFragment fragment = new ForumListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initAdapter() {
        mAdapterList = new ArrayList<>();
        mAdapter = new ForumListAdapter(mAdapterList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
        //设置延时自动刷新数据
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 200);
    }


    protected void getList(final int pageNo) {
        Network
                .getAPIService()
                .getForumList(mCategory.getId(), pageNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String s) {
                        currentPageNO = pageNo;
                        if (currentPageNO == 1) {
                            mAdapterList.clear();
                        }


                        Document doc = Jsoup.parse(s);

                        List<ForumListAtom> list = ForumListAtom.parseFromDocument(doc, mCategory.getId());
                        mAdapterList.addAll(list);

                        mAdapter.notifyDataSetChanged();

                        mPtrFrameLayout.refreshComplete();
                        //第一个参数是：数据是否为空；第二个参数是：是否还有更多数据
                        mLoadMoreListViewContainer.loadMoreFinish(list.isEmpty(), list.size() == PAGE_SIZE);

                        for (ForumListAtom atom : list) {
                            BuglyLog.w("aaaa", atom.toString());
                        }
                    }

                    @Override
                    public void onCompleted() {
                        BuglyLog.i("onCompleted", "----onCompleted----");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                        if (pageNo == 1) {
                            mPtrFrameLayout.refreshComplete();
                        } else {
                            mLoadMoreListViewContainer.loadMoreError(1, "加载失败，请重试");
                        }

                    }
                });
    }
}
