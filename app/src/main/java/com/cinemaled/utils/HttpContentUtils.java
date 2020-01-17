package com.cinemaled.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;

import com.cinemaled.SoftApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * created by fred
 * on 2020/1/14
 */
public class HttpContentUtils {

    public static final int NETWORK_NONE = 0; // 没有网络连接
    public static final int NETWORK_WIFI = 2; // wifi连接
    public static final int NETWORK_2G = 4; // 2G
    public static final int NETWORK_3G = 5; // 3G
    public static final int NETWORK_4G = 6; // 4G
    public static final int NETWORK_MOBILE = 3; // 手机流量

    /**
     * 获取unix时间戳
     *
     * @return
     */
    public static String getT() {
        long currentTimeMillis = System.currentTimeMillis();
        return String.valueOf(currentTimeMillis / 1000);
    }

    /**
     * 获取软件版本号
     *
     * @return
     */
    public static String getver() {
        String localVersion = "";
        try {
            PackageManager packageManager = SoftApplication.instance.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(SoftApplication.instance.getPackageName(), 0);

            localVersion = String.valueOf(packInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getosv() {
        String version = Build.VERSION.SDK_INT + "";
        return version;
    }

    /**
     * 获取android设备id
     *
     * @return
     */
    public static String getandroidid() {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 获取MAC地址
     *
     * @param context
     * @return
     */
    public static String getmac(Context context) {
        String mac = "02:00:00:00:00:00";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacAddress();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }
        return mac;
    }

    /**
     * android7.0以后
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     *
     * @return
     */
    private static String getMacAddress() {
        String WifiAddress = "02:00:00:00:00:00";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     *
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = "02:00:00:00:00:00";
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * 获取设备品牌
     *
     * @return
     */
    public static String getmake() {
        String brand = "";
        brand = android.os.Build.BRAND;
        return brand;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getmodel() {
        String model = "";
        model = android.os.Build.MODEL;
        return model;
    }

    /**
     * 获取当前网络连接的类型
     *
     * @param context context
     * @return int
     */
    public static int getct(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // 获取网络服务
        if (null == connManager) { // 为空则认为无网络
            return NETWORK_NONE;
        }
        // 获取网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }
        // 判断是否为WIFI
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORK_WIFI;
                }
            }
        }
        // 若不是WIFI，则去判断是2G、3G、4G网
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
            // 2G网络
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_2G;
            // 3G网络
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_3G;
            // 4G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_4G;
            default:
                return NETWORK_MOBILE;
        }
    }

    /**
     * 硬件型号版本
     *
     * @return
     */
    public static String gethwv() {
        String id = "";
        id = Build.ID;
        return id;
    }

    /**
     * 获取运营商 mcc+mnc
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getcarrier() {
        TelephonyManager telManager = (TelephonyManager) SoftApplication.instance.getSystemService(TELEPHONY_SERVICE);
        String subscriberId = "";
        subscriberId = telManager.getSubscriberId();
        if (subscriberId.length() > 1) {
            subscriberId = subscriberId.substring(0, 5);
        }
        return subscriberId;
    }

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static String getsh(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return String.valueOf(display.getHeight());
    }

    /**
     * 获取屏幕宽度
     *
     * @param activity
     * @return
     */
    public static String getsw(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return String.valueOf(display.getWidth());
    }


    /**
     * 获取oaid
     *
     * @return
     */
    public static String getoaid() {
//        MiIdHelper miIdHelper = new MiIdHelper();
//        JSONObject result=miIdHelper.getDeviceIds(getApplicationContext());
        return "";
    }

    /**
     * 获取屏幕点亮状态
     *
     * @return
     */
    public static String getss() {
        String state = "0";
        PowerManager powerManager = (PowerManager) SoftApplication.instance.getSystemService(Context.POWER_SERVICE);
        //true为打开，false为关闭
        boolean ifOpen = powerManager.isScreenOn();
        if (ifOpen) {
            state = "1";
        } else {
            state = "0";
        }
        return state;
    }

    /**
     * 获取cpu类型
     *
     * @return
     */
    public static String getcpu() {
        String cpuAbi = Build.CPU_ABI;
        return cpuAbi;
    }


    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return String.valueOf(blockSize * availableBlocks/1024/1024);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static String getRomAvailableSize() {
        ActivityManager am = (ActivityManager) SoftApplication.instance.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return String.valueOf(mi.availMem/1024/1024);
    }

    @SuppressLint("MissingPermission")
    public static String getimei() {
        TelephonyManager tm = (TelephonyManager) SoftApplication.instance.getSystemService(TELEPHONY_SERVICE);
        String imei = "";
        imei = tm.getDeviceId();
        return imei;
    }
}
