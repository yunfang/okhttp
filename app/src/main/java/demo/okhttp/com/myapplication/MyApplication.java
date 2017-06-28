package demo.okhttp.com.myapplication;

import android.content.Context;

import demo.okhttp.com.myapplication.utils.CacheUtils;
import demo.okhttp.com.mylibrary.global.LocalApplication;

/**
 * Created by zhouyunfang on 17/6/28.
 */

public class MyApplication extends LocalApplication {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.mContext = getApplicationContext();

        Config.init(this);

        CacheUtils.init(this);

    }

    public static synchronized Context getContext() {
        return MyApplication.mContext;
    }

}
