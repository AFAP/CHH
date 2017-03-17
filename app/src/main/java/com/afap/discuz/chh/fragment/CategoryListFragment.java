package com.afap.discuz.chh.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afap.discuz.chh.App;
import com.afap.discuz.chh.R;
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
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CategoryListFragment extends Fragment {
    private final static int PAGE_SIZE = 15;
    private final static String KEY_CATEGORY = "key_category";

    private Category mCategory;

    public static CategoryListFragment newInstance(Category category) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_CATEGORY, category);
        System.out.println("111111" + category);
        CategoryListFragment fragment = new CategoryListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private PtrFrameLayout mPtrFrameLayout;
    private LoadMoreListViewContainer mLoadMoreListViewContainer;
    private ListView mListView;
    private List<CategoryListAtom> mAdapterList;
    private CategoryListAdapter mAdapter;


    private int currentPageNO = 1;

    public void setCategory(Category category) {
        mCategory = category;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = (Category) getArguments().getSerializable(KEY_CATEGORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categorylist, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BuglyLog.d("CategoryListFragment", "----onActivityCreated----");
        BuglyLog.d("CategoryListFragment", mCategory.toString());
        // header
        StoreHouseHeader header = new StoreHouseHeader(getContext());
        header.setPadding(0, 10, 0, 10);
        header.initWithString("CHIPHELL");
        header.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        //设置下拉刷新组件和事件监听
        mPtrFrameLayout = (PtrFrameLayout) getView().findViewById(R.id.load_more_list_view_ptr_frame);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1000);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // here check list view, not content.
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //实现下拉刷新的功能
                BuglyLog.i("test", "-----onRefreshBegin-----");

                getList(1);
            }
        });

        //加载更多的组件
        mLoadMoreListViewContainer = (LoadMoreListViewContainer) getView().findViewById(R.id
                .load_more_list_view_container);
        mLoadMoreListViewContainer.setAutoLoadMore(true);//设置是否自动加载更多
        mLoadMoreListViewContainer.useDefaultHeader();
        mLoadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                getList((currentPageNO + 1));
            }
        });

        mListView = (ListView) getView().findViewById(R.id.load_more_listview);
        // 为listview的创建一个headerview,注意，如果不加会影响到加载的footview的显示！
//        View headerMarginView = new View(getActivity());
//        headerMarginView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                LocalDisplay.dp2px(20)));
//        mListView.addHeaderView(headerMarginView);

        mAdapterList = new ArrayList<>();
        mAdapter = new CategoryListAdapter( mAdapterList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryListAtom atom = mAdapterList.get(position);
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

    public CategoryListAtomDao getAtomDao() {
        return App.getInstance().getDaoSession().getCategoryListAtomDao();
    }

    private void getList(final int pageNo) {
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

                        List<CategoryListAtom> list = CategoryListAtom.parseFromElements(doc, mCategory.getId());
                        mAdapterList.addAll(list);

                        getAtomDao().insertInTx(list);

                        mAdapter.notifyDataSetChanged();

                        mPtrFrameLayout.refreshComplete();
                        //第一个参数是：数据是否为空；第二个参数是：是否还有更多数据
                        mLoadMoreListViewContainer.loadMoreFinish(list.isEmpty(), list.size() == PAGE_SIZE);

                        for (CategoryListAtom atom : list) {
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
