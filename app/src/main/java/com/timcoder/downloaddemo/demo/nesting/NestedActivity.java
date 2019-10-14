package com.timcoder.downloaddemo.demo.nesting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.timcoder.downloaddemo.MainActivity;
import com.timcoder.downloaddemo.R;
import com.timcoder.downloaddemo.demo.RestProxy;
import com.timcoder.downloaddemo.demo.polling.TranslateBean;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Administrator
 * 2019/10/14 0014.
 */
public class NestedActivity extends AppCompatActivity {

    private static final String TAG = "NestedRxjava";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nesting_callback);
    }

    // 用rxjava实现嵌套回调以及数据变换
    public void nestingCallback(View view) {
        final Observable<RegisterBean> register = RestProxy.getInstance().getRequestApi().register();
        final Observable<LoginBean> login = RestProxy.getInstance().getRequestApi().login();

        register.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RegisterBean>() {
                    @Override
                    public void accept(RegisterBean registerBean) throws Exception {
                        Log.e(TAG, "register success="+registerBean.toString());
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<RegisterBean, ObservableSource<LoginBean>>() {
                    @Override
                    public ObservableSource<LoginBean> apply(RegisterBean registerBean) throws Exception {
                        if (registerBean.getStatus() == 1) {
                            return login;
                        }
                        return null;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginBean>() {
                    @Override
                    public void accept(LoginBean loginBean) throws Exception {
                        Log.e(TAG, "login success=" + loginBean.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "login failed, error="+throwable.getMessage());
                    }
                });
    }

}
