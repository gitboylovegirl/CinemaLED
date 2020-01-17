package com.cinemaled;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.cinemaled.http.RetrofitApi;
import com.cinemaled.http.RetrofitFactory;
import com.cinemaled.http.UrlApi;
import com.cinemaled.utils.AlertDialogUtils;
import com.cinemaled.utils.HttpContentUtils;
import com.cinemaled.utils.LocationUtils;
import com.cinemaled.utils.MD5Utils;
import com.cinemaled.utils.PreferencesUtil;
import com.cinemaled.utils.ToastUtil;
import com.google.gson.JsonObject;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class CheckActivity extends Activity {
    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        LocationUtils.getInstance().startLocalService(this, new LocationUtils.OnLocationClickListener() {
            @Override
            public void onlocationlistener() {
                num++;
                if (num <= 2 && !PreferencesUtil.getInstance().getPosid().isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //userInit();
                        }
                    }).start();
                }
            }
        });
        AlertDialogUtils.showConfirmDialog(this, new AlertDialogUtils.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick() {
                //点击确认
//                Intent intent = new Intent(CheckActivity.this, MainActivity.class);
//                startActivity(intent);
                userInit();
                //finish();
            }

            @Override
            public void onNegativeButtonClick() {

            }
        });
        requestTDPermission();
        getDeVice();
    }

    /**
     * 申请权限
     */
    public void requestTDPermission() {
        //权限申请
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(CheckActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            }, 101);
        }
    }

    //获取sign信息
    private void getDeVice() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("os", "android");
        jsonObject.addProperty("osv", HttpContentUtils.getosv());
        jsonObject.addProperty("androidid", HttpContentUtils.getandroidid());
        jsonObject.addProperty("mac", HttpContentUtils.getmac(CheckActivity.this));
        jsonObject.addProperty("imei", HttpContentUtils.getimei());
        jsonObject.addProperty("make", HttpContentUtils.getmake());
        jsonObject.addProperty("model", HttpContentUtils.getmodel());
        jsonObject.addProperty("ct", HttpContentUtils.getct(CheckActivity.this));
        if (AccountManager.getInstance().getAdress() == null) {
            jsonObject.addProperty("lon", "");
            jsonObject.addProperty("lat", "");
        } else {
            jsonObject.addProperty("lon", AccountManager.getInstance().getAdress().getLongitude());
            jsonObject.addProperty("lat", AccountManager.getInstance().getAdress().getLatitude());
        }
        jsonObject.addProperty("hwv", HttpContentUtils.gethwv());
        jsonObject.addProperty("carrier", HttpContentUtils.getcarrier());
        jsonObject.addProperty("sh", HttpContentUtils.getsh(CheckActivity.this));
        jsonObject.addProperty("sw", HttpContentUtils.getsw(CheckActivity.this));
        jsonObject.addProperty("oaid", HttpContentUtils.getoaid());
        jsonObject.addProperty("ss", HttpContentUtils.getss());
        jsonObject.addProperty("cpu", HttpContentUtils.getcpu());
        jsonObject.addProperty("df", HttpContentUtils.getRomAvailableSize());
        jsonObject.addProperty("mf", HttpContentUtils.getSDAvailableSize());

        Log.e("fred", "sign:" + jsonObject.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationUtils.getInstance().stopLocalService();
    }

    /**
     * 用户初始化
     */
    private void userInit() {
        RetrofitFactory instance = RetrofitFactory.getInstance();
        RetrofitApi retrofitApi = instance.getT(RetrofitApi.class, UrlApi.TEST_BASE_URL);
        String signInit = AccountManager.getInstance().getSignInit();
        Log.e("fred", AccountManager.getInstance().getAdress().getAddress() + "   " + HttpContentUtils.getT() + "   " + AccountManager.getInstance().getDevice() + "\n    " + signInit);

        String t = HttpContentUtils.getT();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("locid", "");
        jsonObject.put("posid", PreferencesUtil.getInstance().getPosid());
        jsonObject.put("addr", AccountManager.getInstance().getAdress().getAddress());
        jsonObject.put("t",t);
        jsonObject.put("ver", HttpContentUtils.getver());
        jsonObject.put("device",JSONObject.parse(AccountManager.getInstance().getDevice()));
        jsonObject.put("sign", getSignInit(CheckActivity.this,t));
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("Content-Type, application/json"),
                        jsonObject.toString());

        Observable<ResponseBody> baseResponseObservable = retrofitApi.init(requestBody);
        instance.schedule(baseResponseObservable, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(final ResponseBody s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String string = s.string();
                            Log.e("fred", string);
                            JSONObject object = JSONObject.parseObject(string);
                            int code = (int) object.getInteger("code");
                            if (code == 200) {

                            } else {
                                ToastUtil.showShort(CheckActivity.this, object.getString("msg"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    /**
     * 初始化时的sign信息
     */
    private String getSignInit(Activity activity,String time) {
        String posid = PreferencesUtil.getInstance().getPosid();
        String device = AccountManager.getInstance().getDevice();
        String addr = AccountManager.getInstance().getAdress().getAddress();
        String t = time;
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
            strA.append("androidid="+androidid);
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
        Log.e("retrofit", "sign：" + strA);
        String sign = MD5Utils.stringToMD5(strA.toString());
        AccountManager.getInstance().setSignInit(sign);
        return sign;
    }
}
