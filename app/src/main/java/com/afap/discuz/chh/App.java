package com.afap.discuz.chh;

import android.app.Application;

import com.afap.discuz.chh.greendao.DaoMaster;
import com.afap.discuz.chh.greendao.DaoSession;
import com.afap.utils.ToastUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {
    private final String TAG = "App";

    private static App mInstance;

    /* 数据库相关 */
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        Fresco.initialize(this);
        ToastUtil.init(this);

        // TODO 待关闭debug模式
        CrashReport.initCrashReport(getApplicationContext(), "92e0cdb443", true);
        BuglyLog.d(TAG, "BaseApplication初始化");

        // TODO 待关闭调试
//        Stetho.initializeWithDefaults(this);
    }

    public static synchronized App getInstance() {
        return mInstance;
    }


    /**
     * 取得DaoMaster
     */
    private DaoMaster getDaoMaster() {
        if (mDaoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(this, "chh", null);
            mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return mDaoMaster;
    }

    /**
     * 取得DaoSession
     */
    public DaoSession getDaoSession() {
        if (mDaoSession == null) {
            if (mDaoMaster == null) {
                mDaoMaster = getDaoMaster();
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }

}
