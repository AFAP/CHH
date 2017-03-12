package com.afap.discuz.chh.widget.loading;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afap.discuz.chh.R;


public class LoadingView extends FrameLayout {
    public LoadingView(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (View.GONE == visibility && mState == LoadingState.STATE_LOADING && animation != null && animation
                .isRunning()) {
            animation.stop();
        }
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    private Context mContext;
    LinearLayout ll_over;

    LinearLayout ll_loading;
    TextView tv_loaded;
    TextView tv_loading;
    Button btn_loaded;
    ProgressBar pb_load;
    ImageView iv_loaded;

    /**
     * 加载中提示文字
     */
    private String mLoadingText;

    /**
     * 加载数据为空提示文字
     */
    private String mLoaded_empty_text;
    private int mEmptyIco;

    public LoadingView withEmptyIco(int resId) {
        mEmptyIco = resId;
        return this;
    }

    public OnRetryListener mOnRetryListener;

    public LoadingView withOnRetryListener(OnRetryListener mOnRetryListener) {
        this.mOnRetryListener = mOnRetryListener;
        return this;
    }

    private LoadingState mState;

    public void setState(LoadingState state) {
        if (state == LoadingState.STATE_LOADING) {
            ll_over.setVisibility(GONE);
            ll_loading.setVisibility(VISIBLE);
        } else {
            ll_loading.setVisibility(GONE);
            ll_over.setVisibility(VISIBLE);
            if (animation != null && mState == LoadingState.STATE_LOADING)
                animation.stop();
        }
        changeState(state);
    }

    public boolean btn_empty_ennable = true;

    public boolean isNeedReload = true;

    public LoadingView withBtnEmptyEnnable(boolean ennable) {
        btn_empty_ennable = ennable;
        return this;
    }

    public LoadingView withReLoad(boolean isReload) {
        isNeedReload = isReload;
        return this;
    }

    private AnimationDrawable animation;

    private Animation rotation;

    private void changeState(LoadingState state) {
        switch (state) {
            case STATE_LOADING:
                pb_load.setVisibility(View.VISIBLE);
                tv_loading.setText(mLoadingText);
                break;
            case STATE_EMPTY:
                mState = LoadingState.STATE_EMPTY;
                iv_loaded.setImageResource(mEmptyIco);
                tv_loaded.setText(mLoaded_empty_text);
                if (btn_empty_ennable) {
                    btn_loaded.setVisibility(VISIBLE);
                    btn_loaded.setText(btn_empty_text);
                } else {
                    btn_loaded.setVisibility(GONE);
                }
                break;

        }

    }


    public LoadingView withLoadedEmptyText(int resId) {
        mLoaded_empty_text = getResources().getString(resId);
        return this;
    }

    public LoadingView withLoadedEmptyText(String mLoadedemptyText) {
        this.mLoaded_empty_text = mLoadedemptyText;
        return this;
    }

    public String btn_empty_text = "重试";

    public LoadingView withbtnEmptyText(String text) {
        this.btn_empty_text = text;
        return this;
    }

    public LoadingView withLoadingText(int resId) {
        mLoadingText = getResources().getString(resId);
        return this;
    }

    public LoadingView withLoadingText(String mLoadingText) {
        this.mLoadingText = mLoadingText;
        return this;
    }

    public void build() {
        View view = View.inflate(mContext, R.layout.loading, this);

        ll_over = (LinearLayout) view.findViewById(R.id.ll_over);
        ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);
        tv_loaded = (TextView) view.findViewById(R.id.tv_loaded);
        tv_loading = (TextView) view.findViewById(R.id.tv_loading);
        btn_loaded = (Button) view.findViewById(R.id.btn_loaded);
        pb_load = (ProgressBar) view.findViewById(R.id.pb_load);
        iv_loaded = (ImageView) view.findViewById(R.id.iv_loaded);
        btn_loaded.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRetryListener != null) {
                    if (isNeedReload) {
                        setState(LoadingState.STATE_LOADING);
                    } else {
                        setState(LoadingState.STATE_EMPTY);
                    }
                    mOnRetryListener.onRetry();
                }
            }
        });
    }
}
