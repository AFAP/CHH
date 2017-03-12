package com.afap.discuz.chh.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.fragment.CategoryListFragment;
import com.afap.discuz.chh.model.Category;
import com.afap.utils.ContextUtil;
import com.tencent.bugly.crashreport.BuglyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    private List<Category> mCategorys;
    private int mCategoryIndex = 0;

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

        mPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mCategorys.get(0));
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void initData() {
        mCategorys = new ArrayList<>();
        String arrstr = ContextUtil.getStringFromAsset(this, "category.js", null);
        try {
            JSONArray array = new JSONArray(arrstr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);

                Category category = new Category(obj.optString("id"), obj.optString("name"), obj.optInt("type"));
                JSONArray childArr = obj.optJSONArray("childs");

                List<Category> childList = new ArrayList<>();
                for (int j = 0; j < childArr.length(); j++) {
                    JSONObject child = childArr.optJSONObject(j);
                    Category c = new Category(child.optString("id"), child.optString("name"), child.optInt("type"));
                    childList.add(c);
                }
                category.setChildrens(childList);
                mCategorys.add(category);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

        TextView tv_versionName = (TextView) findViewById(R.id.versionName);
        String versionNam = ContextUtil.getAppVersionName(this);
        tv_versionName.setText(String.format(getString(R.string.app_version_format), versionNam));


        ListView mListView = (ListView) findViewById(R.id.category_list);
        final CategoryAdapter mAdapter = new CategoryAdapter(mCategorys);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawers();
                mCategoryIndex = position;
                mAdapter.notifyDataSetChanged();

                mPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mCategorys.get(position));
                mViewPager.setAdapter(mPagerAdapter);
            }
        });
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

    class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
        private Category category;

        public SimpleFragmentPagerAdapter(FragmentManager fm, Category category) {
            super(fm);
            this.category = category;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            CategoryListFragment fragment = (CategoryListFragment) super.instantiateItem(container, position);
            fragment.setCategory(category.getChildrens().get(position));
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {
            return CategoryListFragment.newInstance(category.getChildrens().get(position));
        }

        @Override
        public int getCount() {
            return category.getChildrens().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return category.getChildrens().get(position).getName();
        }
    }


    class CategoryAdapter extends BaseAdapter {

        private List<Category> mData;
        private LayoutInflater mInflater;

        public CategoryAdapter(List<Category> list) {
            mData = list;
        }


        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Category category = mData.get(position);

            ViewHolder mHolder;
            if (convertView == null) {

                mHolder = new ViewHolder();

                if (mInflater == null) {
                    mInflater = LayoutInflater.from(parent.getContext());
                }
                convertView = mInflater.inflate(R.layout.atom_category, null);
                mHolder.atom_img = (ImageView) convertView.findViewById(R.id.atom_img);
                mHolder.atom_name = (AppCompatTextView) convertView.findViewById(R.id.atom_name);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.atom_name.setText(category.getName());
            if (position == mCategoryIndex) {
                mHolder.atom_name.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
            } else {
                mHolder.atom_name.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black_1));
            }

            return convertView;
        }

        class ViewHolder {
            ImageView atom_img;
            AppCompatTextView atom_name;
        }
    }


}
