package cn.gavinliu.bus.station.ui.linedetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.entity.Station;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.widget.BaseAdapter;
import cn.gavinliu.bus.station.widget.BaseListFragment;
import cn.gavinliu.bus.station.widget.BaseViewHolder;
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

public class LineDetailFragment extends BaseListFragment<Station, BaseViewHolder> {

    public static final String KEY_LINE = "KEY_LINE";

    public static LineDetailFragment newInstance(Line line) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_LINE, line);

        LineDetailFragment fragment = new LineDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private CompositeSubscription mSubscriptions;
    private Line mLine;

    private Adapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBus();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSubscriptions.clear();
    }

    private void updateBus() {
        Subscription subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Long aLong) {
                        return BusQueryServiceImpl.getInstance().updateBusForLine(mLine);
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
                        Toast.makeText(getContext(), "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Line line) {
                        mAdapter.setBuses(line.getBuses());
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mLine != null && mLine.getStations() != null) {
            setListData(mLine.getStations());
        }
    }

    @Override
    public void getArguments(Bundle bundle) {
        super.getArguments(bundle);
        if (bundle == null) return;

        mLine = (Line) bundle.getSerializable(KEY_LINE);
    }

    @Override
    protected BaseAdapter createAdapter() {
        mAdapter = new Adapter();
        return mAdapter;
    }

    static class Adapter extends BaseAdapter<Station, Holder> {

        List<Bus> mBuses;

        public void setBuses(List<Bus> buses) {
            mBuses = buses;
            notifyDataSetChanged();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Station station = getItem(position);
            holder.mTitleText.setText(station.getName());

            StringBuilder sb = new StringBuilder();

            if (mBuses != null) {
                for (Bus bus : mBuses) {
                    if (bus.getCurrentStation().equals(station.getName())) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append(bus.getBusNumber());
                    }
                }
            }

            holder.mContentText.setText(sb.toString());
        }
    }

    static class Holder extends BaseViewHolder {

        private TextView mTitleText;
        private TextView mContentText;

        public Holder(View itemView) {
            super(itemView);
            mTitleText = (TextView) itemView.findViewById(R.id.title);
            mContentText = (TextView) itemView.findViewById(R.id.content);
        }
    }

}
