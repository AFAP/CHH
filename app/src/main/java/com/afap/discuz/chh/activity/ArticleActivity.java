package com.afap.discuz.chh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.afap.discuz.chh.Constant;
import com.afap.discuz.chh.R;
import com.afap.discuz.chh.greendao.CategoryListAtom;
import com.afap.discuz.chh.net.BaseSubscriber;
import com.afap.discuz.chh.net.Network;
import com.afap.discuz.chh.widget.loading.LoadingState;
import com.afap.discuz.chh.widget.loading.LoadingView;
import com.afap.discuz.chh.widget.loading.OnRetryListener;
import com.afap.utils.ContextUtil;
import com.tencent.bugly.crashreport.BuglyLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ArticleActivity extends BaseActivity implements View.OnClickListener {

    public final static String KEY_ATOM = "key_href";
    private final static String PRE_HREF = "article-";


    private CategoryListAtom mAtom;
    private WebView mWebView;
    private LoadingView mLoadingView;
    private String mArticleID = "";
    private int totalCommentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mAtom = (CategoryListAtom) getIntent().getSerializableExtra(KEY_ATOM);
        setToolbarTitle(mAtom.getTitle());

        mArticleID = mAtom.getHref().split("-")[1];

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "chhWebView");
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                return true;
            }
        });

        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLoadingView
                .withLoadedEmptyText("未获取到数据")
                .withEmptyIco(0)
                .withBtnEmptyEnnable(true)
                .withLoadingText("加载中...")
                .withbtnEmptyText("再试试")
                .withReLoad(false)
                .withOnRetryListener(new OnRetryListener() {
                    @Override
                    public void onRetry() {
                        mLoadingView.setState(LoadingState.STATE_LOADING);
                        mLoadingView.setVisibility(View.VISIBLE);
                        getDetails();
                    }

                }).build();
        mLoadingView.setState(LoadingState.STATE_LOADING);
        mLoadingView.setVisibility(View.VISIBLE);
        getDetails();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comment_num:
                Intent intent = new Intent(ArticleActivity.this, ArticleCommentActivity.class);
                intent.putExtra(ArticleCommentActivity.KEY_ID, mArticleID);

                startActivity(intent);
                break;
        }
    }

    private void getDetails() {

        final StringBuffer sb = new StringBuffer();
        // 请求第一页
        Network
                .getAPIService()
                .getArticle(PRE_HREF + mArticleID + "-1.html")
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        Document doc = Jsoup.parse(s);

                        try {
                            String _commentnum = doc.getElementById("_commentnum").text();
                            totalCommentCount = Integer.parseInt(_commentnum);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Element div_ct = doc.getElementById("ct");

                        Element div_header = div_ct.getElementsByAttributeValue("class", "h hm").get(0);
                        Element div_zhaiyao = div_ct.getElementsByAttributeValue("class", "s").get(0);

                        sb.append(div_header.html());
                        sb.append(div_zhaiyao.html());

                        Element div_content = div_ct.getElementById("article_content");

                        String str = div_content.toString();
                        str = str.replaceAll("<img", "<img style='width: 100%;'");
                        sb.append(str);


                        Element div_page = div_ct.getElementsByAttributeValue("class", "ptw pbw cl").get(0);

                        Element span_page = div_page.getElementsByTag("span").get(0);
                        String totalPahe = span_page.text();
                        totalPahe = totalPahe.replaceAll("共", "").replaceAll("页", "").replaceAll("/", "").trim();


                        Observable<String> observable = Network
                                .getAPIService()
                                .getArticle(PRE_HREF + mArticleID + "-2.html");

                        int maxPage = Integer.parseInt(totalPahe);
                        if (maxPage > 2) {


                            for (int i = 3; i <= maxPage; i++) {
                                final int tempIndex = i;
                                observable = observable.flatMap(new Func1<String, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(String s) {
                                        Document doc = Jsoup.parse(s);
                                        Element div_ct = doc.getElementById("ct");
                                        Element div_content = div_ct.getElementById("article_content");

                                        String str = div_content.toString();
                                        sb.append(str);

                                        return Network
                                                .getAPIService()
                                                .getArticle(PRE_HREF + mArticleID + "-" + tempIndex + ".html");
                                    }
                                });
                            }

                        } else {

                        }


                        return observable;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        mLoadingView.setVisibility(View.GONE);

                        Document doc = Jsoup.parse(s);
                        Element div_ct = doc.getElementById("ct");

                        Element div_content = div_ct.getElementById("article_content");

                        String str = div_content.html();
                        sb.append(str);


                        StringBuffer headSB = new StringBuffer();
                        headSB.append("<head><script type='text/javascript'>");
                        headSB.append(ContextUtil.getStringFromAsset(ArticleActivity.this, "article_view.js", null));
                        headSB.append("</script></head>");

                        String htmlStr = "<html>" + headSB.toString() + "<body onload='init()'>" + sb.toString() + "</body></html>";

                        mWebView.loadDataWithBaseURL(Constant.HOST_APP, htmlStr, "text/html", "utf-8", null);

                        BuglyLog.i("htmlStr", htmlStr);


                        TextView tv_comment_num = (TextView) findViewById(R.id.tv_comment_num);
                        tv_comment_num.setText(String.valueOf(totalCommentCount));
                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLoadingView.setState(LoadingState.STATE_EMPTY);
                    }
                });
    }

    @android.webkit.JavascriptInterface
    public void jsLoadPictures(final String aaa, final int index) {
        ArrayList<String> picList = new ArrayList<>();
        String[] pics = aaa.split(";");
        for (int i = 0; i < pics.length; i++) {
            picList.add(pics[i]);
        }

        Intent intent = new Intent(this, PicBrowseActivity.class);
        intent.putStringArrayListExtra(PicBrowseActivity.KEY_LIST, picList);
        intent.putExtra(PicBrowseActivity.KEY_INDEX, index);
        startActivity(intent);
    }
}
