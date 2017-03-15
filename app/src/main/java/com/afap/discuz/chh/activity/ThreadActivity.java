package com.afap.discuz.chh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
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
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.bugly.crashreport.BuglyLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ThreadActivity extends BaseActivity implements View.OnClickListener {

    public final static String KEY_ATOM = "key_href";
//    private final static String PRE_HREF = "article-";

    private CategoryListAtom mAtom;
    private WebView mWebView;
    private LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        setToolbarTitle("详细信息");

        mAtom = (CategoryListAtom) getIntent().getSerializableExtra(KEY_ATOM);


        TextView louzhu_title = (TextView) findViewById(R.id.louzhu_title);
        louzhu_title.setText(mAtom.getTitle());

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "chhWebView");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thread, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_floor) {
            Intent intent = new Intent(this, ThreadFloorActivity.class);
            intent.putExtra(ThreadActivity.KEY_ATOM, mAtom);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comment_num:
//                Intent intent = new Intent(ThreadActivity.this, ArticleCommentActivity.class);
//                intent.putExtra(ArticleCommentActivity.KEY_ID, mArticleID);
//
//                startActivity(intent);
                break;
        }
    }

    private void getDetails() {

        final StringBuffer sb = new StringBuffer();
        Network
                .getAPIService()
                .getThread(mAtom.getHref())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        mLoadingView.setVisibility(View.GONE);

                        Document doc = Jsoup.parse(s);
                        // 楼层集合
                        Element div_postlist = doc.getElementById("postlist");

                        Elements div_floors = div_postlist.getElementsByAttributeValueContaining("id", "post_");

                        String mainId = div_floors.get(0).attr("id").replaceAll("post_", "");

                        Element a_name = div_floors.get(0).getElementsByAttributeValue("class", "xw1").get(0);
                        Element a_avatar = div_floors.get(0).getElementsByAttributeValue("class", "avtm").get(0);
                        Element em_time = div_floors.get(0).getElementsByAttributeValueContaining("id",
                                "authorposton").get(0);


                        String name = a_name.text();
                        String url_avatar = a_avatar.child(0).attr("src");
                        String time = em_time.text();

                        SimpleDraweeView louzhu_avatar = (SimpleDraweeView) findViewById(R.id.louzhu_avatar);
                        TextView louzhu_name = (TextView) findViewById(R.id.louzhu_name);
                        TextView louzhu_time = (TextView) findViewById(R.id.louzhu_time);

                        louzhu_avatar.setImageURI(url_avatar);
                        louzhu_name.setText(name);
                        louzhu_time.setText(time);


                        String str = div_floors.get(0).child(0).child(0).child(0).child(1).child(1).html();

//                        str = str.replaceAll("<img", "<img style='width: 100%;'").replaceAll
//                                ("src=\"static/image/common/none.gif\"", "").replaceAll("zoomfile=", "src=");
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
                        headSB.append(ContextUtil.getStringFromAsset(ThreadActivity.this, "thread_view.js", null));
                        headSB.append("</script></head>");


                        String htmlStr = "<html>" + headSB.toString() + "<body onload='" + onload + "' >" +
                                sb.toString() + "</body></html>";

                        mWebView.loadDataWithBaseURL(Constant.HOST_APP, htmlStr, "text/html", "utf-8", null);

                        BuglyLog.i("htmlStr", htmlStr);
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
            picList.add(Constant.HOST_APP + pics[i]);
        }

        Intent intent = new Intent(this, PicBrowseActivity.class);
        intent.putStringArrayListExtra(PicBrowseActivity.KEY_LIST, picList);
        intent.putExtra(PicBrowseActivity.KEY_INDEX, index);
        startActivity(intent);
    }
}
