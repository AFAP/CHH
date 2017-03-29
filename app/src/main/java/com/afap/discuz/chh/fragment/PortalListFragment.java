package com.afap.discuz.chh.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.afap.discuz.chh.activity.ArticleActivity;
import com.afap.discuz.chh.activity.ThreadActivity;
import com.afap.discuz.chh.adapter.CategoryListAdapter;
import com.afap.discuz.chh.greendao.CategoryListAtomDao;
import com.afap.discuz.chh.model.Category;
import com.afap.discuz.chh.greendao.CategoryListAtom;
import com.afap.discuz.chh.net.Network;
import com.tencent.bugly.crashreport.BuglyLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PortalListFragment extends BaseListFragment {
    private final static int PAGE_SIZE = 15;


    public static PortalListFragment newInstance(Category category) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_CATEGORY, category);
        PortalListFragment fragment = new PortalListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initAdapter() {
        mAdapterList = new ArrayList<>();
        mAdapter = new CategoryListAdapter(mAdapterList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryListAtom atom = (CategoryListAtom) mAdapterList.get(position);
                if (mCategory.getType() == Category.TYPE_ARTICLE) {
                    Intent intent = new Intent(getActivity(), ArticleActivity.class);
                    intent.putExtra(ArticleActivity.KEY_ATOM, atom);

                    startActivity(intent);
                } else if (mCategory.getType() == Category.TYPE_THREAD) {
                    Intent intent = new Intent(getActivity(), ThreadActivity.class);
                    intent.putExtra(ThreadActivity.KEY_ATOM, atom);

                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ThreadActivity.class);
                    intent.putExtra(ThreadActivity.KEY_ATOM, atom);

                    startActivity(intent);
                }


            }
        });
        // 从缓存中取得已存在列表
        QueryBuilder qb = getAtomDao().queryBuilder();
        qb.where(CategoryListAtomDao.Properties.Cat.eq(mCategory.getId()));
        List<CategoryListAtom> list = qb.list();
        if (list.size() > 0) {
            mAdapterList.addAll(list);

            mAdapter.notifyDataSetChanged();
        } else {
            //设置延时自动刷新数据
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrameLayout.autoRefresh(false);
                }
            }, 200);
        }
    }


    protected void getList(final int pageNo) {
        Network
                .getAPIService()
                .getList(mCategory.getId(), pageNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String s) {
                        currentPageNO = pageNo;
                        if (currentPageNO == 1) {
                            mAdapterList.clear();

                            // 清除所有数据
                            QueryBuilder qb = getAtomDao().queryBuilder();
                            qb.where(CategoryListAtomDao.Properties.Cat.eq(mCategory.getId()))
                                    .buildDelete().executeDeleteWithoutDetachingEntities();
                        }


                        Document doc = Jsoup.parse(s);

                        List<CategoryListAtom> list = CategoryListAtom.parseFromDocument(doc, mCategory.getId());
                        mAdapterList.addAll(list);

                        getAtomDao().insertInTx(list);

                        mAdapter.notifyDataSetChanged();

                        mPtrFrameLayout.refreshComplete();
                        //第一个参数是：数据是否为空；第二个参数是：是否还有更多数据
                        mLoadMoreListViewContainer.loadMoreFinish(list.isEmpty(), list.size() == PAGE_SIZE);
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
