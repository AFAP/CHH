package com.afap.discuz.chh.net;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface APIService {


    /**
     * 获取最新版本号
     */
//    @GET("portal.php?mod=list&catid={catid}&pege={page}")
//    Observable<String> getList(@Path("catid") String catid, @Path("page") int page);
    @FormUrlEncoded
    @POST("portal.php?mod=list")
    Observable<String> getList(
            @Field("catid") String catid,
            @Field("page") int page
    );

    @GET("{articlehref}")
    Observable<String> getArticle(@Path("articlehref") String articlehref);

    @GET("{href}")
    Observable<String> getThread(@Path("href") String href);


    @FormUrlEncoded
    @POST("portal.php?mod=comment&idtype=aid")
    Observable<String> getArticleComments(
            @Field("id") String id,
            @Field("page") int page
    );


//    @FormUrlEncoded
//    @POST("api.php?act=act_register")
//    Observable<User> register(
//            @Field("username") String username,
//            @Field("password") String password,
//            @Field("user_yzm") String code
//    );

//    @FormUrlEncoded
//    @POST("api_cart.php?act=user_forget_pass")
//    Observable<CommonResult> forgetPwd(
//            @Field("mobile_phone") String mobile_phone,
//            @Field("reset_pass") String reset_pass,
//            @Field("user_yzm") String code
//    );
//
//    @FormUrlEncoded
//    @POST("api.php?act=act_login")
//    Observable<User> login(
//            @Field("username") String username,
//            @Field("password") String password
//    );

}
