package com.cinemaled.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cinemaled.AccountManager;
import com.cinemaled.CheckActivity;
import com.cinemaled.SoftApplication;
import com.cinemaled.bean.LocationBean;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;

public class LocationUtils {
    @SuppressLint("StaticFieldLeak")
    private static AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption = null;

    private static class LocationHolder {
        private static final LocationUtils INSTANCE = new LocationUtils();
    }

    public static LocationUtils getInstance() {
        return LocationHolder.INSTANCE;
    }

    public void startLocalService(final Activity activity, final OnLocationClickListener locationClickListener) {
        //初始化定位
        mLocationClient = new AMapLocationClient(SoftApplication.instance);
        //设置定位回调监听
        mLocationOption = getDefaultOption();
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                if (null != location) {
                    if (location.getErrorCode() == 0) {
                        LocationBean locationBean = new LocationBean();

                        //地址信息
                        String address = location.getCountry() + location.getCity() + location.getAddress();
                        locationBean.setAddress(address);
                        //维度
                        double latitude = location.getLatitude();
                        DecimalFormat df = new DecimalFormat("#.00000");//保留五位小数
                        locationBean.setLatitude(df.format(latitude));

                        //经度
                        double longitude = location.getLongitude();
                        locationBean.setLongitude(df.format(longitude));

                        //地址信息存入缓存
                        AccountManager.getInstance().setAdress(locationBean);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getDeVice(activity);
                                //getSignInit(activity);
                                getSignLogin();
                                if (locationClickListener != null) {
                                    locationClickListener.onlocationlistener();
                                }
                            }
                        }).start();
                    } else {
                        Log.i("location<<<failed", "定位失败\n错误码：" + location.getErrorCode()
                                + "\n错误信息:" + location.getErrorInfo()
                                + "\n错误描述:" + location.getLocationDetail());
                    }
                } else {
                    ToastUtil.showShort(SoftApplication.instance, "定位失败");
                }
            }
        });

        mLocationClient.startLocation();
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

            }
        });
    }

    public void stopLocalService() {
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient.stopLocation();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(6000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    //获取sign信息
    private void getDeVice(Activity activity) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("os", "android");
        jsonObject.addProperty("osv", HttpContentUtils.getosv());
        jsonObject.addProperty("androidid", HttpContentUtils.getandroidid());
        jsonObject.addProperty("mac", HttpContentUtils.getmac(activity));
        jsonObject.addProperty("imei", HttpContentUtils.getimei());
        jsonObject.addProperty("make", HttpContentUtils.getmake());
        jsonObject.addProperty("model", HttpContentUtils.getmodel());
        jsonObject.addProperty("ct", HttpContentUtils.getct(activity));
        jsonObject.addProperty("hwv", HttpContentUtils.gethwv());
        jsonObject.addProperty("carrier", HttpContentUtils.getcarrier());
        jsonObject.addProperty("sh", HttpContentUtils.getsh(activity));
        jsonObject.addProperty("sw", HttpContentUtils.getsw(activity));
        jsonObject.addProperty("oaid", HttpContentUtils.getoaid());
        jsonObject.addProperty("ss", HttpContentUtils.getss());
        jsonObject.addProperty("cpu", HttpContentUtils.getcpu());
        jsonObject.addProperty("df", HttpContentUtils.getSDAvailableSize());
        jsonObject.addProperty("mf", HttpContentUtils.getSDAvailableSize());
        if (AccountManager.getInstance().getAdress() == null) {
            jsonObject.addProperty("lon", "");
            jsonObject.addProperty("lat", "");
        } else {
            jsonObject.addProperty("lon", AccountManager.getInstance().getAdress().getLongitude());
            jsonObject.addProperty("lat", AccountManager.getInstance().getAdress().getLatitude());
        }
        AccountManager.getInstance().setDevice(jsonObject.toString());
    }

    /**
     * 初始化时的sign信息
     */
    private void getSignInit(Activity activity) {
        String posid = PreferencesUtil.getInstance().getPosid();
        String device = AccountManager.getInstance().getDevice();
        String addr = AccountManager.getInstance().getAdress().getAddress();
        String t = HttpContentUtils.getT();
        String ver = HttpContentUtils.getver();
        String androidid = HttpContentUtils.getandroidid();
        String getosv = HttpContentUtils.getosv();
        String getcarrier = HttpContentUtils.getcarrier();
        String getcpu = HttpContentUtils.getcpu();
        int getct = HttpContentUtils.getct(activity);
        String gethwv = HttpContentUtils.gethwv();
        String getimei = HttpContentUtils.getimei();
        String getmac = HttpContentUtils.getmac(activity);
        String getmake = HttpContentUtils.getmake();
        String getmodel = HttpContentUtils.getmodel();
        String getoaid = HttpContentUtils.getoaid();
        String df = HttpContentUtils.getRomAvailableSize();
        String mf = HttpContentUtils.getSDAvailableSize();
        String getsh = HttpContentUtils.getsh(activity);
        String getsw = HttpContentUtils.getsw(activity);
        String getss = HttpContentUtils.getss();
        String os="android";
        String lon=AccountManager.getInstance().getAdress().getLongitude();
        String lat=AccountManager.getInstance().getAdress().getLatitude();

        StringBuilder strA=new StringBuilder();
        if(!androidid.isEmpty()){
            strA.append("android="+androidid);
        }
        if(getcarrier!=null){
            strA.append("&carrier="+getcarrier);
        }
        if(getcpu!=null){
            strA.append("&cpu="+getcpu);
        }
        if(String.valueOf(getct)!=null){
            strA.append("&ct="+getct);
        }
        if(gethwv!=null){
            strA.append("&hwv="+gethwv);
        }
        if(getimei!=null){
            strA.append("&imei="+getimei);
        }
        if(lat!=null){
            strA.append("&lat="+lat);
        }
        if(lon!=null){
            strA.append("&lon="+lon);
        }
        if(getmac!=null){
            strA.append("&mac="+getmac);
        }
        if(getmake!=null){
            strA.append("&make="+getmake);
        }
        if(getmodel!=null){
            strA.append("&mode="+getmodel);
        }
        strA.append("&os=android");
        if(getosv!=null){
            strA.append("&osv="+getosv);
        }
        if(posid!=null){
            strA.append("&posid="+PreferencesUtil.getInstance().getPosid());
        }
        if(getsh!=null){
            strA.append("&sh="+getsh);
        }
        if(getss!=null){
            strA.append("&ss="+getss);
        }
        if(getsw!=null){
            strA.append("&sw="+getsw);
        }
        if(t!=null){
            strA.append("&t="+t);
        }
        if(ver!=null){
            strA.append("&ver="+ver);
        }
        strA.append("&DJT");
        Log.e("fred", "sign：" + strA);
        String sign = MD5Utils.stringToMD5(strA.toString());
        AccountManager.getInstance().setSignInit(sign);
    }

    /**
     * 初始化后的sign信息
     */
    private void getSignLogin() {
        String locid = PreferencesUtil.getInstance().getLocid();
        String posid = "";
        String device = AccountManager.getInstance().getDevice();
        String addr = "";
        String t = HttpContentUtils.getT();
        String ver = HttpContentUtils.getver();

        String strA = "addr=" + addr + "&device=" + device + "&locid=" + locid + "&posid=" + posid + "&t=" + t + "&ver=" + ver + "&DJT";
        String sign = MD5Utils.stringToMD5(strA);
        AccountManager.getInstance().setSignLogin(sign);
    }


    /**
     * 定位回调接口
     */
    private static OnLocationClickListener onLocationListener;

    public void setOnLocationClickListener(OnLocationClickListener onButtonClickListener) {
        this.onLocationListener = onButtonClickListener;
    }

    /**
     * 定位回調
     */
    public interface OnLocationClickListener {
        void onlocationlistener();
    }
}