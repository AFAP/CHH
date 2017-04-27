package com.afap.discuz.chh;

import android.app.Application;

import com.afap.discuz.chh.greendao.DaoMaster;
import com.afap.discuz.chh.greendao.DaoSession;
import com.afap.discuz.chh.model.User;
import com.afap.utils.ToastUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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


    public void saveUser(User user) {
        File userFile = new File(getFilesDir(), "user.txt");

        try {
            //实例化ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFile));
            //将对象写入文件
            oos.writeObject(user);
            oos.flush();
            oos.close();
            BuglyLog.d(TAG, "存储用户完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        File userFile = new File(getFilesDir(), "user.txt");
        if (!userFile.exists()) {
            return null;
        }

        User user = null;

        try {
            //实例化ObjectInputStream对象
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile));

            //读取对象people,反序列化
            user = (User) ois.readObject();
            BuglyLog.d(TAG, "读取用户完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}
