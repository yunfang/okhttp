/*
 *   Copyright (C)  2016 android@19code.com
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package demo.okhttp.com.mylibrary.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by zhouyunfang on 17/6/1.
 */
public class NetUtils {

    private static final String TAG = "net";
    private static final int LOW_SPEED_UPLOAD_BUF_SIZE = 1024;
    private static final int HIGH_SPEED_UPLOAD_BUF_SIZE = 10240;
    private static final int MAX_SPEED_UPLOAD_BUF_SIZE = 102400;
    private static final int LOW_SPEED_DOWNLOAD_BUF_SIZE = 2024;
    private static final int HIGH_SPEED_DOWNLOAD_BUF_SIZE = 30720;
    private static final int MAX_SPEED_DOWNLOAD_BUF_SIZE = 102400;

    public NetUtils() {
    }

    public static boolean hasNetwork(Context var0) {
        if(var0 != null) {
            ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo var2 = var1.getActiveNetworkInfo();
            return var2 != null?var2.isAvailable():false;
        } else {
            return false;
        }
    }

    @TargetApi(13)
    public static boolean hasDataConnection(Context var0) {
        try {
            ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo var2 = var1.getNetworkInfo(1);
            if(var2 != null && var2.isAvailable() && var2.isConnected()) {
                Log.d("net", "has wifi connection");
                return true;
            } else {
                var2 = var1.getNetworkInfo(0);
                if(var2 != null && var2.isAvailable() && var2.isConnected()) {
                    Log.d("net", "has mobile connection");
                    return true;
                } else {
                    if(Build.VERSION.SDK_INT >= 13) {
                        var2 = var1.getNetworkInfo(9);
                        if(var2 != null && var2.isAvailable() && var2.isConnected()) {
                            Log.d("net", "has ethernet connection");
                            return true;
                        }
                    }

                    Log.d("net", "no data connection");
                    return false;
                }
            }
        } catch (Exception var3) {
            return false;
        }
    }

    @Deprecated
    public static boolean isWifiConnection(Context var0) {
        return isWifiConnected(var0);
    }

    public static boolean isWifiConnected(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getNetworkInfo(1);
        if(var2 != null && var2.isAvailable() && var2.isConnected()) {
            Log.d("net", "wifi is connected");
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    public static boolean isMobileConnection(Context var0) {
        return isMobileConnected(var0);
    }

    public static boolean isMobileConnected(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getNetworkInfo(0);
        if(var2 != null && var2.isAvailable() && var2.isConnected()) {
            Log.d("net", "mobile is connected");
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    public static boolean isEthernetConnection(Context var0) {
        return isEthernetConnected(var0);
    }

    public static boolean isEthernetConnected(Context var0) {
        if(Build.VERSION.SDK_INT >= 13) {
            ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo var2 = var1.getNetworkInfo(9);
            if(var2 != null && var2.isAvailable() && var2.isConnected()) {
                Log.d("net", "ethernet is connected");
                return true;
            }
        }

        return false;
    }

    public static String getWiFiSSID(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getNetworkInfo(1);
        WifiManager var3 = (WifiManager)var0.getSystemService(Context.WIFI_SERVICE);
        WifiInfo var4 = var3.getConnectionInfo();
        return var4.getSSID();
    }

    public static int getUploadBufSize(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getActiveNetworkInfo();
        return var2 != null && var2.getType() == 1?102400:(Build.VERSION.SDK_INT >= 13 && var2 != null && var2.getType() == 9?102400:(var2 == null && isConnectionFast(var2.getType(), var2.getSubtype())?10240:1024));
    }

    public static int getDownloadBufSize(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getActiveNetworkInfo();
        return var2 != null && var2.getType() == 1?102400:(Build.VERSION.SDK_INT >= 13 && var2 != null && var2.getType() == 9?102400:(var2 == null && isConnectionFast(var2.getType(), var2.getSubtype())?30720:2024));
    }

    private static boolean isConnectionFast(int var0, int var1) {
        if(var0 == 1) {
            return true;
        } else if(Build.VERSION.SDK_INT >= 13 && var0 == 9) {
            return true;
        } else {
            if(var0 == 0) {
                switch(var1) {
                    case 1:
                        return false;
                    case 2:
                        return false;
                    case 3:
                        return true;
                    case 4:
                        return false;
                    case 5:
                        return true;
                    case 6:
                        return true;
                    case 7:
                        return false;
                    case 8:
                        return true;
                    case 9:
                        return true;
                    case 10:
                        return true;
                    default:
                        if(Build.VERSION.SDK_INT >= 11 && (var1 == 14 || var1 == 13)) {
                            return true;
                        }

                        if(Build.VERSION.SDK_INT >= 9 && var1 == 12) {
                            return true;
                        }

                        if(Build.VERSION.SDK_INT >= 8 && var1 == 11) {
                            return false;
                        }
                }
            }

            return false;
        }
    }

    public static String getNetworkType(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager)var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getActiveNetworkInfo();
        if(var2 != null && var2.isAvailable()) {
            int var3 = var2.getType();
            if(Build.VERSION.SDK_INT >= 13 && var3 == 9) {
                return "ETHERNET";
            } else if(var3 == 1) {
                return "WIFI";
            } else {
                TelephonyManager var4 = (TelephonyManager)var0.getSystemService(Context.TELEPHONY_SERVICE);
                switch(var4.getNetworkType()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return "2G";
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return "3G";
                    case 13:
                        return "4G";
                    default:
                        return "unkonw network";
                }
            }
        } else {
            return "no network";
        }
    }
}
