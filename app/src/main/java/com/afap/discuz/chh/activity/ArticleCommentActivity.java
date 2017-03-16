package com.afap.discuz.chh.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.adapter.ArticleCommentAdapter;
import com.afap.discuz.chh.model.ArticleComment;
import com.afap.discuz.chh.net.BaseSubscriber;
import com.afap.discuz.chh.net.Network;
import com.afap.utils.ToastUtil;
import com.tencent.bugly.crashreport.BuglyLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ArticleCommentActivity extends BaseActivity implements View.OnClickListener {
    public final static String KEY_ID = "key_id";

    private final static int PAGE_SIZE = 25;

    private String mArticleID;

    private PtrFrameLayout mPtrFrameLayout;
    private LoadMoreListViewContainer mLoadMoreListViewContainer;
    private ListView mListView;
    private List<ArticleComment> mAdapterList;
    private ArticleCommentAdapter mAdapter;


    private int currentPageNO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_comment);

        setToolbarTitle(R.string.app_name);
        mArticleID = getIntent().getStringExtra(KEY_ID);


        // header
        StoreHouseHeader header = new StoreHouseHeader(this);
        header.setPadding(0, 10, 0, 10);
        header.initWithString("CHIPHELL");
        header.setTextColor(ContextCompat.getColor(this, R.color.black));

        //设置下拉刷新组件和事件监听
        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.load_more_list_view_ptr_frame);
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
                getList(1);
            }
        });

        //加载更多的组件
        mLoadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id
                .load_more_list_view_container);
        mLoadMoreListViewContainer.setAutoLoadMore(true);//设置是否自动加载更多
        mLoadMoreListViewContainer.useDefaultHeader();
        mLoadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                getList((currentPageNO + 1));
            }
        });

        mListView = (ListView) findViewById(R.id.load_more_listview);
        // 为listview的创建一个headerview,注意，如果不加会影响到加载的footview的显示！
        View headerMarginView = new View(this);
        headerMarginView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                LocalDisplay.dp2px(20)));
        mListView.addHeaderView(headerMarginView);

        mAdapterList = new ArrayList<>();
        mAdapter = new ArticleCommentAdapter(mAdapterList);
        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            }
//        });
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 200);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                ToastUtil.showShort(R.string.todev);
                break;
        }
    }

    private void getList(final int pageNo) {
        Network
                .getAPIService()
                .getArticleComments(mArticleID, pageNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        currentPageNO = pageNo;
                        if (currentPageNO == 1) {
                            mAdapterList.clear();
                        }

                        Document doc = Jsoup.parse(s);

                        List<ArticleComment> list = ArticleComment.parseFromElements(doc);
                        mAdapterList.addAll(list);

                        mAdapter.notifyDataSetChanged();

                        mPtrFrameLayout.refreshComplete();
                        //第一个参数是：数据是否为空；第二个参数是：是否还有更多数据
                        mLoadMoreListViewContainer.loadMoreFinish(list.isEmpty(), list.size() == PAGE_SIZE);
                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        if (pageNo == 1) {
                            mPtrFrameLayout.refreshComplete();
                        } else {
                            mLoadMoreListViewContainer.loadMoreError(1, "加载失败，请重试");
                        }

                    }
                });
    }

}
