package demo.okhttp.com.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;

import demo.okhttp.com.myapplication.utils.CacheUtils;
import demo.okhttp.com.myapplication.utils.ConstantUtils;
import demo.okhttp.com.myapplication.utils.FileUtils;
import demo.okhttp.com.mylibrary.bean.BaseResult;
import demo.okhttp.com.mylibrary.net.HttpCallBack;
import demo.okhttp.com.mylibrary.net.HttpHelper;
import demo.okhttp.com.mylibrary.utils.ToastUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button id_net_bu;
    private TextView id_cache_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id_net_bu = (Button) findViewById(R.id.id_net_bu);
        id_net_bu.setOnClickListener(this);


        Button id_start_bu = (Button) findViewById(R.id.id_start_bu);
        id_start_bu.setOnClickListener(this);

        Button id_cache_bu = (Button) findViewById(R.id.id_cache_bu);
        id_cache_bu.setOnClickListener(this);

        Button id_start_cache = (Button) findViewById(R.id.id_start_cache);
        id_start_cache.setOnClickListener(this);

        Button id_start_cache_clear = (Button) findViewById(R.id.id_start_cache_clear);
        id_start_cache_clear.setOnClickListener(this);

        id_cache_tv = (TextView) findViewById(R.id.id_cache_tv);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_net_bu:

                HashMap<String, Object> stringObjectHashMap = new HashMap<>();
                stringObjectHashMap.put("action","categoryList");
                stringObjectHashMap.put("uuid","38bc1a11c8ff");
                HttpHelper.doGetRequest(this, "https://www.yunfangjsj.com/json/v3.1.2.json", stringObjectHashMap, CotegoryBean.class, new HttpCallBack<CotegoryBean>() {
                    @Override
                    public void onSuccess(String json, CotegoryBean baseResult) {
                        ToastUtils.showToast(json);

                        CacheUtils.getObjectCache().add("CotegoryBean", baseResult, ConstantUtils.MILLS_PER_DAY);

                    }

                    @Override
                    public void onError(String json, CotegoryBean baseResult) {
                        ToastUtils.showToast(json);
                        CacheUtils.getObjectCache().add("CotegoryBean", baseResult, ConstantUtils.MILLS_PER_DAY);

                    }
                });

                break;
            case R.id.id_start_bu:
                PackageManager packageManager = this.getPackageManager();

                Intent it= packageManager.getLaunchIntentForPackage("com.tencent.mm");
                startActivity(it);

                break;
            case R.id.id_cache_bu:
                CotegoryBean cotegoryBean = (CotegoryBean) CacheUtils.getObjectCache().get("CotegoryBean");
                ToastUtils.showToast(cotegoryBean.getCategorys().get(0).getName());
                break;
            case R.id.id_start_cache:

                String autoFileOrFilesSizeTwo = FileUtils.getAutoFileOrFilesSizeTwo(new File(Config.getObjectCacheFolderPath()));
                id_cache_tv.setText(autoFileOrFilesSizeTwo);

                break;
            case R.id.id_start_cache_clear:
                FileUtils.deleteDirectory(Config.getObjectCacheFolderPath());



                String autoFileOrFilesSizeTwo1 = FileUtils.getAutoFileOrFilesSizeTwo(new File(Config.getObjectCacheFolderPath()));
                id_cache_tv.setText(autoFileOrFilesSizeTwo1);
                break;
        }

    }
}
