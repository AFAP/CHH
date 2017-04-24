package com.afap.discuz.chh.net;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface APIService {


    /**
     * 获取门户列表
     */
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

    @FormUrlEncoded
    @POST("member.php?mod=logging&action=login&loginsubmit=yes&frommessage&loginhash=LijIJ&inajax=1")
    Observable<String> login(
            @Field("formhash") String formhash,
            @Field("referer") String referer,
            @Field("username") String username,
            @Field("password") String password,
            @Field("questionid") String questionid,
            @Field("seccodehash") String seccodehash,
            @Field("seccodemodid") String seccodemodid,
            @Field("seccodeverify") String seccodeverify
    );


    @GET("forum.php")
    Observable<String> getForumMain();

    /**
     * 获取论坛列表
     */
    @GET("forum-{id}-{page}.html")
    Observable<String> getForumList(@Path("id") String articlehref, @Path("page") int page);


    /**
     * 获取登录页面初始信息
     */
    @GET("member.php?mod=logging&action=login")
    Observable<String> getLoginInitInfo();

    /**
     * 获取登录页面初始信息
     */
    @GET("misc.php?mod=seccode&action=update")
    Observable<String> getCodeInfo(@Query("idhash") String idhash, @Query("upload") double upload);

    @GET
    Observable<ResponseBody> getCodeImage(@Url String fileUrl);

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
