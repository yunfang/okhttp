package demo.okhttp.com.mylibrary.utils;

import android.text.TextUtils;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import com.orhanobut.logger.Logger;

import demo.okhttp.com.mylibrary.global.Constants;


/**
 * Author: 墨色玄清丶
 * *
 * Date: 2016-06-04 13:03
 * *
 * QQ: 363246266
 * *
 * Version: V1.0
 */
public class LogUtils {
    private static final int mLogLevel = Constants.DEV_DEBUG ? 5 : -1;

    public static void logE(String message) {
        if (mLogLevel > 4) {
            Log.e("tzy_message", " " + message);
        }
    }

    public static void logW(String message) {
        if (mLogLevel > 3) {
            Log.w("tzy_message", " " + message);
        }
    }

    public static void logI(String message) {
        if (mLogLevel > 2) {
            Log.i("tzy_message", " " + message);
        }
    }

    public static void logV(String message) {
        if (mLogLevel > 1) {
            Log.v("tzy_message", " " + message);
        }
    }

    public static void logE(Class clazz, String message) {
        if (mLogLevel > 4) {
            Log.e(clazz.getSimpleName(), " " + message);
        }
    }

    public static void logW(String TAG, String message) {
        if (mLogLevel > 3) {
            Log.w(TAG, " " + message);
        }
    }

    public static void logI(String TAG, String message) {
        if (mLogLevel > 2) {
            Log.i(TAG, " " + message);
        }
    }

    public static void logV(String TAG, String message) {
        if (mLogLevel > 1) {
            Log.v(TAG, " " + message);
        }
    }


    //防止json数据多长，2000个长度分割一次
    public static void logContent(String tag, String content) {
        if (mLogLevel > 3) {
            int p = 2000;
            long length = content.length();
            if (length < p || length == p)
                Log.w(tag, content);
            else {
                while (content.length() > p) {
                    String logContent = content.substring(0, p);
                    content = content.replace(logContent, "");
                    Log.w(tag, logContent);
                }
                Log.w(tag, content);
            }
        }
    }

    //防止json数据多长，2000个长度分割一次
    public static void logContentE(String tag, String content) {
        if (mLogLevel > 4) {
            int p = 2000;
            long length = content.length();
            if (length < p || length == p)
                Log.e(tag, content);
            else {
                while (content.length() > p) {
                    String logContent = content.substring(0, p);
                    content = content.replace(logContent, "");
                    Log.e(tag, logContent);
                }
                Log.e(tag, content);
            }
        }
    }

    public static void LoggerE(String content) {
        if (mLogLevel > 4) {
            Logger.wtf(content);
        }
    }

    public static void Logger_NetWork(String message) {
        if (mLogLevel > 3) {
            if (message.contains("--") || (message.contains("=") && !message.contains("{") && !message.contains("Content-Type") && !message.contains("Set-Cookie"))) {
                if (message.contains("--")) {
                    logW(message);
                    if (message.contains("END HTTP")) {
                        logW("══════════════════════════════════════════════════════════════════════════════════════════════════");
                        logW(" ");
                    }
                } else
                    logW("OkHttpParam: " + message);
            } else if (message.contains("{")) {
                String s = null;
                try {
                    JSONObject dataJsonObject = new JSONObject(message);
                    s = dataJsonObject.toString(4);
                } catch (JSONException e) {
                }
                LogUtils.logContent("tzy_message", " OkHttpResult: " + (TextUtils.isEmpty(s) ? message : s));
            }
        }
    }
}
