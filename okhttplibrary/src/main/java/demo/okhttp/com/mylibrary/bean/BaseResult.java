package demo.okhttp.com.mylibrary.bean;

import java.io.Serializable;

/**
 * Created by zhouyunfang on 17/6/1.
 */

public class BaseResult implements Serializable {


    private String Code;


    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
