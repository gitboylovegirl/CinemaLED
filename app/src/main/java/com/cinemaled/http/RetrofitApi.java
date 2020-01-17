package com.cinemaled.http;

import com.cinemaled.OBDDialogP;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * created by fred
 * on 2019/9/23
 */
public interface RetrofitApi {
    //版本更新
    @GET("upgrade/reminder")
    Observable<String> update(@Query("phoneType") String phoneType, @Query("versionCode") String versionNumber);

    @POST("business/user/codeLogin/{phone}/{code}")
    Observable<ResponseBody> loginbysms(@Path("phone") String phone, @Path("code") String code);


    @POST("/business/user/pwdLogin/{phone}/{pwd}")
    Observable<ResponseBody> loginbyPwd(@Path("phone") String phone, @Path("pwd") String pwd);


    @POST("business/base/listMessage")
    Observable<ResponseBody> getMessageData();

    @FormUrlEncoded
    @POST("business/base/businessAllDiscuss")
    Observable<ResponseBody> getDiscuss(@Field("businessid") String businessid, @Field("type") String pwd);

    @POST("business/base/getBusinessCount/{businessId}")
    Observable<ResponseBody> getBusinessCount(@Path("businessId") String businessId);

    /**
     * 初始化
     * @return
     */
    //@FormUrlEncoded
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("init")
    Observable<ResponseBody> init(@Body RequestBody requestBody);


    //    @PUT("userSubmit/feedback")
//    Observable<FeedEntry> feed(@QueryMap HashMap<String, String> map);
//    //获取banner数据
//    @GET("banner/getPhoneBannerList")
//    Observable<BannerEntry> getBanner();
//    //房间举报(包含单文件上传)
//    @PUT("userSubmit/feedback")
//    Observable<FeedEntry> report(@Part MultipartBody.Part file, @QueryMap HashMap<String, String> map);
//    //获取首页列表
//    @GET("channels/all")
//    Observable<HomeListEntry> homeData(@Query("p") String page, @Query("size") String size);
}
