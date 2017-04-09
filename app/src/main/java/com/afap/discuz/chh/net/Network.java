package com.afap.discuz.chh.net;

import com.afap.discuz.chh.Constant;
import com.tencent.bugly.crashreport.BuglyLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Network {
    private static APIService apis;
    public static Map<String, String> mUrlMap = new HashMap<>();

    private static String codeurl = "";

    public static APIService getAPIService() {
        if (apis == null) {
            // TODO 最后关闭日志
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp3.0的使用方式
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            String reqUrl = request.url().toString().replaceAll(Constant.HOST_APP, "");

                            Request.Builder builder = request
                                    .newBuilder()
                                    .addHeader("contentType", "text/html; charset=utf-8")
                                    // 这个很重要
                                    .addHeader("user-agent", "Chrome/56.0.2924.87 Safari/537.36");

                            // 如果是请求登录图片验证码的请求，需要记录下来，且在请求验证码图片时设置referer
                            if (reqUrl.contains("mod=seccode&action=update")) {
                                codeurl = request.url().toString();
                            } else if (reqUrl.contains("mod=seccode&update=")) {
                                builder.addHeader("referer", codeurl);
                            }

                            Request build = builder.build();
                            Response response = chain.proceed(build);
                            String rspUrl = response.request().url().toString().replaceAll(Constant.HOST_APP, "");
                            mUrlMap.put(reqUrl, rspUrl);

                            return response;
                        }
                    })
                    .cookieJar(new CookieJar() {
                        List<Cookie> cookielist = new ArrayList<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            for (int i = 0; i < cookies.size(); i++) {
                                BuglyLog.w("saveFromResponse", cookies.get(i).toString());
                                if (!cookielist.contains(cookies.get(i))) {
                                    cookielist.add(cookies.get(i));
                                }

                            }
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            BuglyLog.d("loadForRequest", "--");
                            for (int i = 0; i < cookielist.size(); i++) {
                                BuglyLog.w("loadForRequest", cookielist.get(i).toString());
                            }
                            return cookielist;
                        }
                    })
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.HOST_APP)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
            apis = retrofit.create(APIService.class);
        }
        return apis;
    }

}