package cn.gavinliu.bus.station.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.utils.AlarmChecker;
import cn.gavinliu.bus.station.utils.EventCaster;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by gavin on 2017/2/20.
 */

public class AlarmService extends Service {

    private static final String TAG = AlarmService.class.getSimpleName();

    public static final String KEY_ACTION = "KEY_ACTION";

    public static final int ACTION_LINE_ALARM = 1;
    public static final int ACTION_LINE_DETAIL = 2;

    private Subscription mAlarmChecker;
    private Subscription mLineDetail;
    private Line mLine;

    private AlarmManager mAlarmManager;

    private Handler mHandler = new Handler(getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        EventCaster.getInstance().register(this);
        mAlarmManager = AlarmManager.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventCaster.getInstance().unregister(this);
        Log.d(TAG, "onDestroy");
    }

    @Subscribe
    public void onAlarmFinish(AlarmChecker.AlarmCheckerEvent event) {
        Log.d(TAG, "onAlarmFinish");

        AlarmManager.getInstance().finish();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(KEY_ACTION, 0);

        switch (action) {
            case ACTION_LINE_ALARM: {
                Line line = (Line) intent.getSerializableExtra("LINE");
                if (line == null) break;

                if (mLine != null && mLine.getId().equals(line.getId()) && mLineDetail != null) {
                    mLineDetail.unsubscribe();
                }

                Log.d(TAG, "ACTION_LINE_ALARM");

                startAlarm(line);
                break;
            }

            case ACTION_LINE_DETAIL: {
                Line line = (Line) intent.getSerializableExtra("LINE");
                if (line == null) break;
                mLine = line;

                if (!mLine.getId().equals(mAlarmManager.getLineId())) {
                    updateBus(line);
                }
                break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBus(final Line line) {
        if (mLineDetail != null) mLineDetail.unsubscribe();

        mLineDetail = Observable.interval(0, 5, TimeUnit.SECONDS)
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
    }

    private void startAlarm(final Line line) {
        if (mAlarmChecker != null) mAlarmChecker.unsubscribe();

        mAlarmChecker = Observable.interval(0, 5, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Long aLong) {
                        return BusQueryServiceImpl.getInstance().updateBusForLine(line);
                    }
                })
                .flatMap(new AlarmChecker())
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
    }
}
