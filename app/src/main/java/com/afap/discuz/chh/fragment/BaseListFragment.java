package com.afap.discuz.chh.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.afap.discuz.chh.App;
import com.afap.discuz.chh.R;
import com.afap.discuz.chh.greendao.CategoryListAtomDao;
import com.afap.discuz.chh.model.Category;
import com.tencent.bugly.crashreport.BuglyLog;

import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;


public class BaseListFragment extends Fragment {
    protected final static String KEY_CATEGORY = "key_category";

    protected Category mCategory;

    protected PtrFrameLayout mPtrFrameLayout;
    protected LoadMoreListViewContainer mLoadMoreListViewContainer;
    protected ListView mListView;


    protected int currentPageNO = 1;

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
        BuglyLog.v("BaseListFragment", "----onActivityCreated----");
        BuglyLog.v("BaseListFragment", mCategory.toString());
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

        initAdapter();

    }

    protected void initAdapter() {
    }


    protected void getList(final int pageNo) {

    }

    protected CategoryListAtomDao getAtomDao() {
        return App.getInstance().getDaoSession().getCategoryListAtomDao();
    }
}
