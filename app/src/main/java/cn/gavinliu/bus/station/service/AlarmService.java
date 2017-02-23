package cn.gavinliu.bus.station.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.utils.EventCaster;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by gavin on 2017/2/20.
 */

public class AlarmService extends Service {

    private static final String TAG = AlarmService.class.getSimpleName();

    private CompositeSubscription mSubscriptions;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mSubscriptions.clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Line line = (Line) intent.getSerializableExtra("LINE");
        if (line != null) {
            updateBus(line);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBus(final Line line) {
        mSubscriptions.clear();
        Subscription subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Long aLong) {
                        return BusQueryServiceImpl.getInstance().updateBusForLine(line);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Line>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Line line) {
                        EventCaster.getInstance().post(line);
                    }
                });
        mSubscriptions.add(subscription);
    }

}
