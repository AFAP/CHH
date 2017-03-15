package com.afap.discuz.chh.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.adapter.ThreadFloorAdapter;
import com.afap.discuz.chh.greendao.CategoryListAtom;
import com.afap.discuz.chh.model.ThreadFloor;
import com.afap.discuz.chh.net.BaseSubscriber;
import com.afap.discuz.chh.net.Network;
import com.afap.utils.ContextUtil;
import com.tencent.bugly.crashreport.BuglyLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

/**
 * 帖子各楼层
 */
public class ThreadFloorActivity extends BaseActivity {

    private final static int PAGE_SIZE = 30;

    private CategoryListAtom mAtom;

    private PtrFrameLayout mPtrFrameLayout;
    private LoadMoreListViewContainer mLoadMoreListViewContainer;
    private ListView mListView;
    private List<ThreadFloor> mAdapterList;
    private ThreadFloorAdapter mAdapter;

    private int currentPageNO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_comment);

        setToolbarTitle(R.string.app_name);
        mAtom = (CategoryListAtom) getIntent().getSerializableExtra(ThreadActivity.KEY_ATOM);

        // header
        StoreHouseHeader header = new StoreHouseHeader(this);
        header.setPadding(0, 10, 0, 10);
        header.initWithString("CHIPHELL");
        header.setTextColor(ContextCompat.getColor(this, R.color.black));

        //设置下拉刷新组件和事件监听
        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.load_more_list_view_ptr_frame);
        mPtrFrameLayout.setLoadingMinTime(2000);
        mPtrFrameLayout.setDurationToCloseHeader(2000);
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
        mAdapter = new ThreadFloorAdapter(mAdapterList);
        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            }
//        });
        mPtrFrameLayout.autoRefresh();
    }


    private void getList(final int pageNo) {
        String href = mAtom.getHref();
        String tarhref = pageNo + "-1.html";
        href = href.replaceAll("1-1.html", tarhref);




        Network
                .getAPIService()
                .getThread(href)
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


                        // 楼层集合
                        Element div_postlist = doc.getElementById("postlist");


                        Elements div_floors = div_postlist.getElementsByAttributeValueMatching("id", "post_\\d{4,}");

                        for (int i = 1; i < div_floors.size(); i++) {

                            StringBuffer sb = new StringBuffer();

                            String mainId = div_floors.get(i).attr("id").replaceAll("post_", "");

                            try {
                                Element a_name = div_floors.get(i).getElementsByAttributeValue("class", "xw1").get(0);
                                Element a_avatar = div_floors.get(i).getElementsByAttributeValue("class", "avtm").get
                                        (0);
                                Element em_time = div_floors.get(i).getElementsByAttributeValueContaining("id",
                                        "authorposton").get(0);


                                String name = a_name.text();
                                String url_avatar = a_avatar.child(0).attr("src");
                                String time = em_time.text();


                                String str = div_floors.get(i).child(0).child(0).child(0).child(1).child(1).html();

                                sb.append(str);


                                String onload = "";
                                String imgdata = "";
                                Elements scripts = doc.getElementsByAttributeValue("reload", "1");
                                for (Element element : scripts) {
                                    if (TextUtils.equals(element.tagName(), "script")) {
                                        String aaa = element.html();
                                        if (aaa.indexOf("attachimggroup(" + mainId + ")") != -1) {
                                            String[] arr = aaa.split(";");
                                            imgdata = arr[0].trim();
                                            onload = arr[1].trim();
                                        }
                                        BuglyLog.w("aaa", aaa);
                                    }
                                }


                                StringBuffer headSB = new StringBuffer();
                                headSB.append("<head><script type='text/javascript'>");
                                headSB.append("var aimgcount = {};");
                                headSB.append(imgdata + ";");
                                headSB.append(ContextUtil.getStringFromAsset(ThreadFloorActivity.this, "thread_view.js",
                                        null));
                                headSB.append("</script></head>");


                                String htmlStr = "<html>" + headSB.toString() + "<body onload='" + onload + "' >" +
                                        sb.toString() + "</body></html>";
                                ThreadFloor item = new ThreadFloor();
                                item.setName(name);
                                item.setAvatarUrl(url_avatar);
                                item.setTime(time);
                                item.setFloorNum((i + 1));
                                item.setContentHtml(htmlStr);
                                mAdapterList.add(item);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                continue;
                            }


                        }


                        mAdapter.notifyDataSetChanged();

                        mPtrFrameLayout.refreshComplete();
                        //第一个参数是：数据是否为空；第二个参数是：是否还有更多数据
                        boolean hasMore = false;
                        if (pageNo == 1 && mAdapterList.size() == PAGE_SIZE - 1) {
                            hasMore = true;
                        } else if (pageNo > 1 && mAdapterList.size() == PAGE_SIZE) {
                            hasMore = true;
                        }

                        mLoadMoreListViewContainer.loadMoreFinish(mAdapterList.isEmpty(), hasMore);
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