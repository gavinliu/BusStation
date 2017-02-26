package cn.gavinliu.bus.station.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.ui.linedetail.LineDetailActivity;
import cn.gavinliu.bus.station.utils.AlarmChecker;
import cn.gavinliu.bus.station.utils.EventCaster;
import cn.gavinliu.bus.station.utils.ScreenUtils;
import cn.gavinliu.bus.station.widget.AlertLayout;
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
    public static final int ACTION_DETAIL_FINISH = 3;

    private static final int NOTIFICATION_ID = 100;

    private Handler mHandler;

    private Line mLine;
    private Subscription mLineDetail;

    private Line mCheckerLine;
    private Subscription mAlarmChecker;

    private Ringtone mRingtone;
    private WindowManager mWindowManager;
    private NotificationManager mNotificationManager;

    private AlertLayout mAlertLayout;

    private AlarmManager mAlarmManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        EventCaster.getInstance().register(this);
        mAlarmManager = AlarmManager.getInstance();

        mHandler = new Handler();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        EventCaster.getInstance().unregister(this);
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
                mCheckerLine = line;

                if (mLine != null && mLine.getId().equals(line.getId()) && mLineDetail != null) {
                    mLineDetail.unsubscribe();
                }

                Log.d(TAG, "ACTION_LINE_ALARM");

                startAlarm(line);
                Notification notification = createNotification();
                startForeground(NOTIFICATION_ID, notification);
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

            case ACTION_DETAIL_FINISH: {
                if (mLineDetail != null) {
                    mLineDetail.unsubscribe();
                }

                if (!mAlarmManager.alarmEnable()) {
                    stopSelf();
                }
                break;
            }


        }

        return START_NOT_STICKY;
    }

    @Subscribe
    public void onAlarmFinish(AlarmChecker.AlarmCheckerEvent event) {
        Log.d(TAG, "onAlarmFinish");

        if (mAlarmChecker != null) {
            mAlarmChecker.unsubscribe();
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mNotificationManager.cancel(NOTIFICATION_ID);
                stopForeground(true);
                showAlertLayout();
                AlarmManager.getInstance().finish();
            }
        });
    }

    @Subscribe
    public void updateNotification(final Line line) {
        if (line == null) return;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAlarmManager.alarmEnable()) {
                    mNotificationManager.notify(NOTIFICATION_ID, createNotification());
                }
            }
        });
    }

    private void showAlertLayout() {
        if (mRingtone == null) createRingtone();

        mRingtone.play();

        if (mAlertLayout == null) createAlertLayout();
        WindowManager.LayoutParams layoutParams = createWindowLayoutParams();

        mWindowManager.addView(mAlertLayout, layoutParams);
    }

    private void closeAlertLayout() {
        if (mRingtone != null) mRingtone.stop();
        mWindowManager.removeView(mAlertLayout);
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

    private void createAlertLayout() {
        mAlertLayout = (AlertLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.alert_layout, null, false);
        mAlertLayout.setItemListener(new AlertLayout.ItemListener() {
            @Override
            public void onCloseClick() {
                closeAlertLayout();
            }
        });
    }

    private void createRingtone() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
    }

    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        windowLayoutParams.format = PixelFormat.RGBA_8888;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        windowLayoutParams.flags = windowLayoutParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowLayoutParams.alpha = 1.0f;
        windowLayoutParams.gravity = Gravity.START | Gravity.TOP;

        ScreenUtils screenUtils = ScreenUtils.getInstance();

        windowLayoutParams.x = getResources().getDimensionPixelSize(R.dimen.alert_layout_margin_LR);
        windowLayoutParams.y = screenUtils.getHeight() / 3;
        windowLayoutParams.width = screenUtils.getWidth() - getResources().getDimensionPixelSize(R.dimen.alert_layout_margin_LR) * 2;
        windowLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.alert_layout_height);

        return windowLayoutParams;
    }

    private Notification createNotification() {
        String content = null;

        if (mCheckerLine != null && mCheckerLine.getBuses() != null) {
            List<Bus> buses = mCheckerLine.getBuses();
            for (Bus bus : buses) {
                if (bus.getBusNumber().equals(AlarmManager.getInstance().getBusNumber())) {
                    content = bus.getBusNumber() + " 已经开到 " + bus.getCurrentStation();
                }
            }
        }

        return new NotificationCompat.Builder(getApplication())
                .setContentTitle("到站提醒: " + AlarmManager.getInstance().getStationName())
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentIntent(createContentIntent())
                .build();
    }

    private PendingIntent createContentIntent() {
        Intent intent = new Intent(this, LineDetailActivity.class);
        intent.putExtra(LineDetailActivity.KEY_LINE, mCheckerLine);
        return PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
