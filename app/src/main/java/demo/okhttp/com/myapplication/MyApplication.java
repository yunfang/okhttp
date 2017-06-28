package demo.okhttp.com.myapplication;

import demo.okhttp.com.myapplication.utils.CacheUtils;
import demo.okhttp.com.mylibrary.global.LocalApplication;

/**
 * Created by zhouyunfang on 17/6/28.
 */

public class MyApplication extends LocalApplication {

    @Override
    public void onCreate() {
        super.onCreate();


        Config.init(this);

        CacheUtils.init(this);



    }
}
