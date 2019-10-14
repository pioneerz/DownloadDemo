package com.timcoder.downloaddemo.demo;


import com.timcoder.downloaddemo.demo.nesting.LoginBean;
import com.timcoder.downloaddemo.demo.nesting.RegisterBean;
import com.timcoder.downloaddemo.demo.polling.TranslateBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Administrator
 * 2019/10/14 0014.
 */
public interface ApiService {

    @GET("ajax.php?a=fy&f=auto&t=auto")
    Observable<TranslateBean> getCall(@Query("w") String world);

    @GET("ajax.php?a=fy&f=auto&t=auto&w=hello%20register")
    Observable<RegisterBean> register();

    @GET("ajax.php?a=fy&f=auto&t=auto&w=hello%20login")
    Observable<LoginBean> login();

}
