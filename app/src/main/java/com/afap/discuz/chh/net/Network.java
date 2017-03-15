package com.afap.discuz.chh.net;

import com.afap.discuz.chh.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Network {
    private static APIService apis;

    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
    private static OkHttpClient okHttpClient;

    public static APIService getAPIService() {
        if (apis == null) {
            // TODO 最后关闭日志
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp3.0的使用方式
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Request.Builder builder1 = request.newBuilder();
                            Request build = builder1
                                    .addHeader("contentType", "text/html; charset=utf-8")
                                    .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                                    .build();

                            return chain.proceed(build);
                        }
                    })
                    .cookieJar(new CookieJar() {
                        List<Cookie> cookielist;

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            this.cookielist = cookies;
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            return cookielist != null ? cookielist : new ArrayList<Cookie>();
                        }
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.HOST_APP)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .client(okHttpClient)
                    .build();
            apis = retrofit.create(APIService.class);
        }
        return apis;
    }

}