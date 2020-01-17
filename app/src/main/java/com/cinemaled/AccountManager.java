package com.cinemaled;

import com.cinemaled.bean.LocationBean;

/**
 * created by fred
 * on 2020/1/14
 */
public class AccountManager {
    private static AccountManager mInstance;
    public static AccountManager getInstance() {
        if (mInstance == null) {
            mInstance = new AccountManager();
        }

        return mInstance;
    }

    //当前地址实体类
    private LocationBean adress;
    private String device;
    private String signLogin;
    private String signInit;

    public LocationBean getAdress() {
        return adress;
    }

    public void setAdress(LocationBean adress) {
        this.adress = adress;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSignLogin() {
        return signLogin;
    }

    public void setSignLogin(String signLogin) {
        this.signLogin = signLogin;
    }

    public String getSignInit() {
        return signInit;
    }

    public void setSignInit(String signInit) {
        this.signInit = signInit;
    }
}
