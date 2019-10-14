package com.timcoder.downloaddemo.demo.polling;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.timcoder.downloaddemo.R;
import com.timcoder.downloaddemo.demo.RestProxy;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Administrator
 * 2019/10/14 0014.
 */
public class PollingActivity extends AppCompatActivity {

    private EditText mEditText;
    private static final String TAG = "polling";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_polling);
        initView();
    }

    private void initView() {
        mEditText = findViewById(R.id.edit_text);
    }

    public void pollingTranslate(View view) {
        Observable.interval(2, 1, TimeUnit.SECONDS)
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        // TODO 可以作为网络请求
                        String searchWord = mEditText.getText().toString();
                        Observable<TranslateBean> observable = RestProxy.getInstance().getRequestApi().getCall(searchWord);
                        observable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<TranslateBean>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Log.e(TAG, "inner onSubscribe");
                                    }

                                    @Override
                                    public void onNext(TranslateBean value) {
                                        Log.e(TAG, "inner value=" + value.toString());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "inner onError=" + e.getMessage());
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.e(TAG, "inner onComplete");
                                    }
                                });
                    }
                })
                .subscribe(new Observer<Long>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        Log.e(TAG, "outer onSubscribe");
                    }

                    @Override
                    public void onNext(Long value) {
                        Log.e(TAG, "outer onNext=" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "outer onError=" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "outer onComplete");
                    }
                });
    }
}
