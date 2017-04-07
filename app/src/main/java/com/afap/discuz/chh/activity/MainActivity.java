package com.afap.discuz.chh.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afap.discuz.chh.Constant;
import com.afap.discuz.chh.R;
import com.afap.discuz.chh.adapter.CategoryAdapter;
import com.afap.discuz.chh.fragment.BaseListFragment;
import com.afap.discuz.chh.fragment.PortalListFragment;
import com.afap.discuz.chh.fragment.ForumListFragment;
import com.afap.discuz.chh.model.Category;
import com.afap.discuz.chh.net.BaseSubscriber;
import com.afap.discuz.chh.net.Network;
import com.afap.discuz.chh.widget.loading.LoadingState;
import com.afap.utils.ContextUtil;
import com.afap.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.bugly.crashreport.BuglyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    private List<Category> mCategorys;
    private int mCategoryIndex = 1;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SimpleFragmentPagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarTitle(R.string.app_name);

        initData();
        initLeftMenu();

        mPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mCategorys.get(1));
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

//        getForumNum();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_library) {
            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 连续按两次返回键就退出
     */
    private long firstTime = 0;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - firstTime < 2000) {
            this.finish();
        } else {
            firstTime = System.currentTimeMillis();
            ToastUtil.showShort(this, R.string.tip_exit);
        }
    }

    private void initData() {
        mCategorys = new ArrayList<>();

        String arrstr = ContextUtil.getStringFromAsset(this, "category.js", null);
        try {
            JSONArray array = new JSONArray(arrstr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);

                Category category = new Category(obj.optString("id"), obj.optString("name"), obj.optInt("type"), obj
                        .optBoolean("islabel", false));
                JSONArray childArr = obj.optJSONArray("childs");

                List<Category> childList = new ArrayList<>();
                for (int j = 0; j < childArr.length(); j++) {
                    JSONObject child = childArr.optJSONObject(j);
                    Category c = new Category(child.optString("id"), child.optString("name"), child.optInt("type"),
                            obj.optBoolean("islabel", false));
                    childList.add(c);
                }
                category.setChildrens(childList);
                mCategorys.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TODO 默认显示第一个栏目
        mCategorys.get(1).setSelected(true);
    }

    CategoryAdapter adapter;

    /**
     * 初始化左侧菜单
     */
    private void initLeftMenu() {
        // 实现左侧侧滑菜单
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        // 创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
                R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mCategorys.get(position).isLabel() ? 3 : 1;
            }
        });
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new CategoryAdapter(this, mCategorys, new CategoryAdapter.OnClickListener() {
            @Override
            public void click(int position) {
                mDrawerLayout.closeDrawers();
                // 重复点击过滤掉不处理
                if (mCategoryIndex == position) {
                    return;
                }
                mCategorys.get(mCategoryIndex).setSelected(false);
                mCategoryIndex = position;
                mCategorys.get(mCategoryIndex).setSelected(true);

                adapter.notifyDataSetChanged();

                mPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mCategorys.get(position));
                mViewPager.setAdapter(mPagerAdapter);
                // 如果没有子分类，没有必要显示TabLayout
                if (mCategorys.get(position).getChildrens().size() > 0) {
                    mTabLayout.setVisibility(View.VISIBLE);
                } else {
                    mTabLayout.setVisibility(View.GONE);
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
//                RxGalleryFinal
//                        .with(this)
//                        .image()
//                        .radio()
//                        .crop()
//                        .imageLoader(ImageLoaderType.FRESCO)
//                        .subscribe(new RxBusResultSubscriber<ImageRadioResultEvent>() {
//                            @Override
//                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
//                                //图片选择结果
//                                BuglyLog.w("ImageRadioResultEvent", imageRadioResultEvent.getResult().getCropPath());
//
//
//                            }
//                        })
//                        .openGallery();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);

                break;
        }
    }


    /**
     * 获取论坛各个栏目的今日帖子数量
     */
    private void getForumNum() {
        Network
                .getAPIService()
                .getForumMain()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        Document doc = Jsoup.parse(s);

                        Elements tds = doc.getElementsByAttributeValue("class", "fl_g");


                        for (int i = 0; i < tds.size(); i++) {
                            Element dt = tds.get(i).child(1).child(0);
                            if (dt.childNodeSize() == 2) {
                                String href = dt.child(0).attr("href");
                                String id = href.replaceAll("forum-", "").replaceAll("-1.html", "");
                                String num = dt.child(1).text().replaceAll("\\(", "").replaceAll("\\)", "").trim();

                                BuglyLog.v("MAIN", "id=" + id + ",num=" + num);
                                for (int j = 0; j < mCategorys.size(); j++) {
                                    if (mCategorys.get(j).getType() == Category.TYPE_FORUM && TextUtils.equals(id,
                                            mCategorys.get(j).getId())) {
                                        mCategorys.get(j).setNum(num);
                                        break;
                                    }
                                }
                            }
                        }
//                        // 动态获取菜单
//                        JSONArray array = new JSONArray();
//                        for (int i = 0; i < tds.size(); i++) {
//                            Element dt = tds.get(i).child(1).child(0);
//                            if (dt.childNodeSize() == 2) {
//                                String href = dt.child(0).attr("href");
//                                String id = href.replaceAll("forum-", "").replaceAll("-1.html", "");
//                                String name = dt.child(0).text();
//                                JSONObject object = new JSONObject();
//                                try {
//                                    object.put("id", id);
//                                    object.put("name", name);
//                                    object.put("type", "3");
//                                    object.put("childs", new JSONArray());
//                                    array.put(object);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                        BuglyLog.v("MAIN", array.toString());

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 后台刷新，错误不处理
                    }
                });


    }


    private List<Fragment> fragments = new ArrayList<>();

    // 注意有不少栏目没有子节点，需要判断
    class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private Category category;

        public SimpleFragmentPagerAdapter(FragmentManager fm, Category category) {
            super(fm);
            FragmentTransaction ft = fm.beginTransaction();
            for (int i = 0; i < fragments.size(); i++) {
                ft.remove(fragments.get(i));
            }

            ft.commitNowAllowingStateLoss();
            fragments.clear();
            this.category = category;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseListFragment fragment = (BaseListFragment) super.instantiateItem(container, position);
            fragment.setCategory(category.getChildrens().size() == 0 ? category : category.getChildrens().get
                    (position));
            return fragment;

        }

        @Override
        public Fragment getItem(int position) {
            Category c = category.getChildrens().size() == 0 ? category : category.getChildrens().get(position);
            Fragment fragment;
            if (c.getType() == Category.TYPE_FORUM) {
                fragment = ForumListFragment.newInstance(c);
            } else {
                fragment = PortalListFragment.newInstance(c);
            }
            fragments.add(fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return category.getChildrens().size() == 0 ? 1 : category.getChildrens().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return category.getChildrens().size() == 0 ? category.getName() : category.getChildrens().get(position)
                    .getName();
        }
    }

}
