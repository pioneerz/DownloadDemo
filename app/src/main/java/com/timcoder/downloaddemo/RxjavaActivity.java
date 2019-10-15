package com.timcoder.downloaddemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.timcoder.downloaddemo.demo.nesting.NestedActivity;
import com.timcoder.downloaddemo.demo.polling.PollingActivity;
import com.timcoder.downloaddemo.demo.polling.TranslateBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Administrator
 * 2019/10/10 0010.
 */
public class RxjavaActivity extends AppCompatActivity {

    private static final String TAG = "Rxjava";

    private int mInteger = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rxjava);
    }

    public void openRxjava(View view) {
        // TODO 创建操作符
        // TODO 快速创建 create/just/fromArray/fromIterable/empty/error/never/
        // TODO 延迟创建 defer/timer/interval/intervalRange/range/rangLong


        // 创建被观察者
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(0);
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });

        // TODO just适合创建10以内
//        observable = Observable.just(4, 5, 6);

        // TODO fromArray适合10个以上
        // observable = Observable.fromArray(new int[]{7, 8, 9});

//        List<Integer> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            list.add(i);
//        }
//        observable = Observable.fromIterable(list);

//        observable = Observable.empty();
//        observable = Observable.error(new Throwable("error"));
//        observable = Observable.never();

        // TODO 动态创建被观察者&获取最新的observable对象数据
//        observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
//            @Override
//            public ObservableSource<? extends Integer> call() throws Exception {
//                return Observable.just(mInteger);
//            }
//        });
//        mInteger = 15;

        // 创建观察者
        Observer<Integer> observer = new Observer<Integer>() {

            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe");
                disposable = d;
            }

            @Override
            public void onNext(Integer value) {
                Log.e(TAG, "onNext value=" + value);
                if (value == 2 && disposable != null) {
                    disposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError=" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        };

        observable.subscribe(observer);

//        observable.subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.e("zangdianbin", "value=" + integer);
//            }
//        });

        // TODO 延迟指定事件，发送一个0， 一般用于检测, 本质=延迟N秒后，调用一次onNext(0)
        Observable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe");
            }

            @Override
            public void onNext(Long value) {
                Log.e(TAG, "value=" + value + "   Thread=" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        });

        // todo intervalRange(3,10, 4, 1).subscribe(new Observer(...))
//        Observable.interval(4, 1, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
//            private Disposable disposable;
//
//            @Override
//            public void onSubscribe(Disposable d) {
//                disposable = d;
//                Log.e(TAG, "interval onSubscribe");
//            }
//
//            @Override
//            public void onNext(Long value) {
//                Log.e(TAG, "interval value=" + value + "   Thread=" + Thread.currentThread().getName());
//                if (disposable != null && value == 20) {
//                    disposable.dispose();
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, "interval onError");
//            }
//
//            @Override
//            public void onComplete() {
//                Log.e(TAG, "interval onComplete");
//            }
//        });

        // todo 没有延迟时间, 与rangeLong类似
        Observable.range(3, 10)
                // 该例子发送的事件序列特点：从3开始发送，每次发送事件递增1，一共发送10个事件
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "开始采用subscribe连接");
                    }
                    // 默认最先调用复写的 onSubscribe（）

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "对Complete事件作出响应");
                    }

                });

    }

    public void pollingDemo(View view) {
        try {
            Intent intent = new Intent(this, PollingActivity.class);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    // TODO 变化操作符 map/flatMap/concatMap/buffer; 应用场景：数据类型转换, 嵌套回调
    public void translateOperator(View view) {
        // TODO 将被观察者发送的事件转换为任意类型的事件
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(0);
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) throws Exception {
                return integer + 5;
            }
        }).map(new Function<Integer, TranslateBean>() {
            @Override
            public TranslateBean apply(Integer integer) throws Exception {
                TranslateBean bean = new TranslateBean();
                TranslateBean.ContentBean contentBean = new TranslateBean.ContentBean();
                contentBean.setFrom(String.valueOf(integer));
                contentBean.setOut(String.valueOf(integer + mInteger));
                bean.setContent(contentBean);
                bean.setStatus(integer);
                return bean;
            }
        }).map(new Function<TranslateBean, String>() {
            @Override
            public String apply(TranslateBean translateBean) throws Exception {
                if (translateBean != null) {
                    return translateBean.toString();
                }
                return "null";
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                // 观察者接收到的事件，是变换后的事件
//                Log.e(TAG, "accept=" + s);
            }
        });

        // TODO flatMap无序的将被观察者发送的整个事件序列进行变换; concatMap:有序将被观察者发送的整个时间序列进行变换
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(0);
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add(String.valueOf(integer + mInteger * integer));
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
//                Log.e(TAG, "accept flatMap=" + s);
            }
        });


        // 被观察者 需要发送5个数字
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 1) // 设置缓存区大小 & 步长
                // 缓存区大小 = 每次从被观察者中获取的事件数量
                // 步长 = 每次获取新事件的数量
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Integer> stringList) {
                        Log.e(TAG, " 缓存区里的事件数量 = " + stringList.size());
                        for (Integer value : stringList) {
                            Log.e(TAG, " 事件 = " + value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "对Complete事件作出响应");
                    }
                });

    }

    public void nestedDemo(View view) {
        try {
            Intent intent = new Intent(this, NestedActivity.class);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    /**
     * 组合/合并操作符:组合多个被观察者&合并需要发送的事件
     * concat/concatArray:
     * merge/mergeArray:
     * concatDelayError/mergeDelayError:
     *
     * zip:
     * combineLatest:
     * combineLatestDelayError:
     * reduce:
     * collect:
     * startWith/startWithArray:
     * count:
     * @param view
     */
    public void combineOperator(View view) {
        // concat(size<=4)/concatArray(size>4) 按发送顺序组合多个被观察者一起发送数据，合并后按发送顺序串行执行
        Observable.concat(Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
//                        Log.e(TAG, "comnine concat value=" + integer);
                    }
                });
        // merge(size<=4)/mergeArray(size>4) 按时间线组合多个被观察者一起发送数据，合并后并行执行
        Observable.merge(
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                Observable.intervalRange(2, 3, 1, 1, TimeUnit.SECONDS))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
//                        Log.e(TAG, "Combine merge value=" + aLong);
                    }
                });

        // concatDelayError/mergeDelayError 当组合多个被观察者时，其中一个出现onError时会中断其他被观察者发送事件
        // ...

        // zip合并多个被观察者中发送的事件:严格按照原先的顺序进行对位合并,数量=多个被观察者中事件最少的那个数量
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    emitter.onNext(1);
                    Thread.sleep(1000);
                    emitter.onNext(2);
                    Thread.sleep(1000);
                    emitter.onNext(3);
                    Thread.sleep(1000);
//                    emitter.onNext(4);
//                    Thread.sleep(1000);
//                    emitter.onNext(5);
//                    Thread.sleep(1000);
//                    emitter.onNext(6);
//                    Thread.sleep(1000);
                    emitter.onComplete();
                } catch (InterruptedException e) {
//                    Log.e(TAG, "observable1 error=" + e.getMessage());
                }
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    emitter.onNext("A");
                    Thread.sleep(1000);
                    emitter.onNext("B");
                    Thread.sleep(1000);
                    emitter.onNext("C");
                    Thread.sleep(1000);
                    emitter.onNext("D");
                    Thread.sleep(1000);
                    emitter.onComplete();
                } catch (InterruptedException e) {
//                    Log.e(TAG, "observable2 error=" + e.getMessage());
                }
            }
        }).subscribeOn(Schedulers.newThread());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
//                Log.e(TAG, "zip value=" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
//                Log.e(TAG, "zip error=" + throwable.getMessage());
            }
        });

        // combineLatest 当两个observable中任何一个发送了数据后, 将先发送数据的obsersable的最新(最后)的一个数据与另外一个observable的每一个数据结合，形成新的事件序列
        Observable.combineLatest(Observable.just(1L, 2L, 3L)
                /*Observable.intervalRange(1L, 3, 1, 1, TimeUnit.SECONDS)*/, // 不确定性太大
                Observable.intervalRange(0, 4, 1, 1, TimeUnit.SECONDS),
                new BiFunction<Long, Long, Long>() {
                    @Override
                    public Long apply(Long aLong, Long aLong2) throws Exception {
//                        Log.e(TAG, "aLong1=" + aLong + "  aLong2=" + aLong2);
                        return aLong + aLong2;
                    }
                }).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long value) throws Exception {
//                Log.e(TAG, "combineLatest value=" + value);
            }
        });

        // combineLatestDelayError和concatDelayError作用类似

        // reduce:把呗观察者需要发送的事件聚合成一个是事件&发送
        Observable.just(1, 2, 3, 4)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer * integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
//                Log.e(TAG, "reduce value=" + integer);
            }
        });

        // collect 将被观察者发送的数据事件收集到一个数据结构里
        Observable.just(1, 2, 3, 4, 5, 6)
                .collect(new Callable<ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<ArrayList<Integer>, Integer>() {
                    @Override
                    public void accept(ArrayList<Integer> integers, Integer integer) throws Exception {
                        integers.add(integer);
                    }
                }).subscribe(new Consumer<ArrayList<Integer>>() {
            @Override
            public void accept(ArrayList<Integer> integers) throws Exception {
//                Log.e(TAG, "size=" + integers.size());
            }
        });

        // startWith/startWithArray: 发送事件钱追加发送事件
        // 1.追加一些数据
        Observable.just(4, 5, 6)
                .startWith(0)
                .startWithArray(1, 2, 3)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
//                        Log.e(TAG, "startWith value=" + integer);
                    }
                });
        // 2.追加发送被观察者发送数据
        Observable.just(4, 5, 6)
                .startWith(Observable.just(1, 2, 3))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
//                        Log.e(TAG, "startWith Observable value=" + integer);
                    }
                });

        // count:统计被观察者发送事件的数量
        Observable.just(1, 2, 3, 4)
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "count value=" + aLong);
                    }
                });
    }

}
