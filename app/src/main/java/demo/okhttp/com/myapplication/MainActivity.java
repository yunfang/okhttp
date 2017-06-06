package demo.okhttp.com.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

import demo.okhttp.com.mylibrary.bean.BaseResult;
import demo.okhttp.com.mylibrary.net.HttpCallBack;
import demo.okhttp.com.mylibrary.net.HttpHelper;
import demo.okhttp.com.mylibrary.utils.ToastUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button id_net_bu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id_net_bu = (Button) findViewById(R.id.id_net_bu);
        id_net_bu.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_net_bu:
                HttpHelper.doGetRequest(this, "http://www.yunfangjsj.com/json/jsondemo.json", new HashMap<String, Object>(), BaseResult.class, new HttpCallBack<BaseResult>() {
                    @Override
                    public void onSuccess(String json, BaseResult baseResult) {
                        ToastUtils.showToast(json);
                    }

                    @Override
                    public void onError(String json, BaseResult baseResult) {
                        ToastUtils.showToast(json);

                    }
                });

                break;
        }
    }
}
