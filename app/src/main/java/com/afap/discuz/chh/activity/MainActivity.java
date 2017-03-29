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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.adapter.CategoryAdapter;
import com.afap.discuz.chh.fragment.BaseListFragment;
import com.afap.discuz.chh.fragment.PortalListFragment;
import com.afap.discuz.chh.fragment.ForumListFragment;
import com.afap.discuz.chh.model.Category;
import com.afap.utils.ContextUtil;
import com.tencent.bugly.crashreport.BuglyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;

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
        }

        return super.onOptionsItemSelected(item);
    }


    private void initData() {
        mCategorys = new ArrayList<>();

        String arrstr = ContextUtil.getStringFromAsset(this, "category.js", null);
        try {
            JSONArray array = new JSONArray(arrstr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);

                Category category = new Category(obj.optString("id"), obj.optString("name"), obj.optInt("type"), obj.optBoolean("islabel", false));
                JSONArray childArr = obj.optJSONArray("childs");

                List<Category> childList = new ArrayList<>();
                for (int j = 0; j < childArr.length(); j++) {
                    JSONObject child = childArr.optJSONObject(j);
                    Category c = new Category(child.optString("id"), child.optString("name"), child.optInt("type"), obj.optBoolean("islabel", false));
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
                RxGalleryFinal
                        .with(this)
                        .image()
                        .radio()
                        .crop()
                        .imageLoader(ImageLoaderType.FRESCO)
                        .subscribe(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                //图片选择结果
                                BuglyLog.w("ImageRadioResultEvent", imageRadioResultEvent.getResult().getCropPath());


                            }
                        })
                        .openGallery();
                break;
        }
    }

    private List<Fragment> fragments = new ArrayList<>();
    // 注意有不少栏目没有子节点，需要判断
    class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private Category category;

        public SimpleFragmentPagerAdapter(FragmentManager fm, Category category) {
            super(fm);
            FragmentTransaction ft = fm.beginTransaction();
            for (int i = 0; i <fragments.size() ; i++) {
                ft.remove(fragments.get(i));
            }

            ft.commitNowAllowingStateLoss();
            fragments.clear();
            this.category = category;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseListFragment fragment = (BaseListFragment) super.instantiateItem(container, position);
            fragment.setCategory(category.getChildrens().size() == 0 ? category : category.getChildrens().get(position));
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
            return category.getChildrens().size() == 0 ? category.getName() : category.getChildrens().get(position).getName();
        }
    }


}
