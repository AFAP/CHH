package com.afap.discuz.chh.activity;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.utils.Util;
import com.afap.utils.ToastUtil;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.List;

import me.relex.photodraweeview.PhotoDraweeView;

public class PicBrowseActivity extends Activity {
    public static final String KEY_INDEX = "index";
    public static final String KEY_LIST = "list";

    private List<String> mPics;
    private int mIndex;

    private ViewPager mViewPager;// 页卡内容

    private TextView tv_index;
    private ImageView btn_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_browser);


        mPics = getIntent().getStringArrayListExtra(KEY_LIST);
        mIndex = getIntent().getIntExtra(KEY_INDEX, 0);

        tv_index = (TextView) findViewById(R.id.index);
        btn_download = (ImageView) findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicture();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mViewPager.setCurrentItem(mIndex);
        tv_index.setText((mIndex + 1) + "/" + mPics.size());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 滑动页面适配器
     */
    public class MyPagerAdapter extends PagerAdapter {

        public MyPagerAdapter() {

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            final PhotoDraweeView photoDraweeView = new PhotoDraweeView(view.getContext());
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setUri(Uri.parse(mPics.get(position)));
            controller.setOldController(photoDraweeView.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDraweeView.setController(controller.build());

            try {
                view.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return photoDraweeView;
        }

        @Override
        public int getCount() {
            return mPics.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    /**
     * 滑动页面监听
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {


        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            mIndex = position;
            tv_index.setText((mIndex + 1) + "/" + mPics.size());
        }
    }

    private void savePicture() {
        String imgUrl = mPics.get(mIndex);
        String picName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(imgUrl))
                .build();
        CacheKey cacheKey = DefaultCacheKeyFactory
                .getInstance()
                .getEncodedCacheKey(imageRequest, Uri.parse(imgUrl));
        BinaryResource bRes = ImagePipelineFactory
                .getInstance()
                .getMainFileCache()
                .getResource(cacheKey);

        if (bRes == null) {
            return;
        }
        File file = ((FileBinaryResource) bRes).getFile();
        if (file != null) {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CHH");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Util.copyFile(file, dir, picName);
            ToastUtil.showShort(String.format(getString(R.string.tip_pic_saved_format), dir.getPath()));
        }
    }


}