package demo.okhttp.com.myapplication.utils;

import android.os.Build;

/**
 * Created by zhouyunfang on 17/6/6.
 * @version 1.0.0
 */
public class SDKVersionUtils {

    /**
     * hasForyo
     * @return true false
     */
    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * hasGingerbread
     * @return true false
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * hasHoneycomb
     * @return true false
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * hasHoneycombMR1
     * @return true false
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * hasHoneycombMR2
     * @return true false
     */
    public static boolean hasHoneycombMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    /**
     * hasIceCreamSandwich
     * @return true false
     */
    public static boolean hasIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * hasJellyBean
     * @return true false
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 4.2以上
     * @return true false
     */
    public static boolean aboveJellyBean() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN;
    }
}
