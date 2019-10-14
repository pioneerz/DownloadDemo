package com.timcoder.downloaddemo.demo;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author ymh
 * @version V1.0
 * @date 2019/4/1 17:32
 */
public class RestProxy {
    public final String API_BASE_URL = "http://fy.iciba.com/";

    private static RestProxy instance = new RestProxy();

    public static RestProxy getInstance() {
        return instance;
    }

    private Retrofit mRetrofit;
    private ApiService mRestApi;

    private RestProxy() {
        //okBuilder.addInterceptor(new CustomInterceptor());
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        mRetrofit = builder.build();
        mRestApi = mRetrofit.create(ApiService.class);
    }

    public ApiService getRequestApi() {
        return mRestApi;
    }

    private static class CustomInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            //TODO 日志拦截，记得删除
            Request request = chain.request();
            Response response = chain.proceed(request);
            ResponseBody responseBody = response.peekBody(1024 * 1024);

            Log.e("RestProxy", "responseBody=" + responseBody.string());
            return response;
        }
    }
}
