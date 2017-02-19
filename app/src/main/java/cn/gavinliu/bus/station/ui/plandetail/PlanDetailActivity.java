package cn.gavinliu.bus.station.ui.plandetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.gavinliu.bus.station.db.Plan;
import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.widget.BaseActivity;
import cn.gavinliu.bus.station.utils.CalculateSoonBus;
import cn.gavinliu.zhuhai.station.R;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by gavin on 2017/2/17.
 */

public class PlanDetailActivity extends BaseActivity {

    private static final String TAG = PlanDetailActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    private CompositeSubscription mSubscriptions;

    private ArrayList<Line> mLines;

    private Plan mPlan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        mSubscriptions = new CompositeSubscription();

        Intent intent = getIntent();
        if (intent != null) {
            mPlan = (Plan) intent.getSerializableExtra("PLAN");

            mLines = mPlan.getLines();
            updateStation(mLines);
            updateBus(mLines);
            for (Line line : mLines) {
                Log.d(TAG, line.getName() + " " + line);
            }

        }

        String str = "从 " + mPlan.getStation() + " " + mPlan.getName();

        getSupportActionBar().setTitle(str);

        mAdapter = new Adapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setLines(mLines);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.clear();
    }

    private void updateStation(List<Line> lines) {
        Subscription subscription = BusQueryServiceImpl.getInstance().updateStationForLine(lines)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Line>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Line line) {
                        if (line.getStations() != null) {
                            Log.d(TAG, line.getName() + ":" + line.getStations().get(0).getName() + " " + line);
                        }
                    }
                });

        mSubscriptions.add(subscription);
    }

    private void updateBus(final List<Line> lines) {
        Subscription subscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Long aLong) {
                        return BusQueryServiceImpl.getInstance().updateBusForLine(lines);
                    }
                })
                .flatMap(new CalculateSoonBus())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Line>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Line line) {
                        if (line.getBuses() != null) {
                            for (Bus bus : line.getBuses()) {

                                Log.d(TAG, line.getName() + ":" + bus.getBusNumber() + " - " + bus.getCurrentStation() + " " + line);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                });

        mSubscriptions.add(subscription);
    }

    static class Adapter extends RecyclerView.Adapter<Holder> {

        private List<Line> mLines;

        public void setLines(List<Line> lines) {
            mLines = lines;
            notifyDataSetChanged();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_bus, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Line line = mLines.get(position);

            StringBuilder sb = new StringBuilder();

            sb.append(line.getName()).append("\n");

            if (line.getDist() > -1) {
                sb.append(line.getSoonBus().getBusNumber())
                        .append(" - ")
                        .append(line.getSoonBus().getCurrentStation());
                sb.append("\n");
                sb.append("离本站还有：");
                sb.append(line.getDist());
                sb.append("站");
            }

            holder.mTextView.setText(sb.toString());
        }

        @Override
        public int getItemCount() {
            return mLines != null ? mLines.size() : 0;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public Holder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
