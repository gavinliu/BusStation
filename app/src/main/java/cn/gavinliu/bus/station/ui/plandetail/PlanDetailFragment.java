package cn.gavinliu.bus.station.ui.plandetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.gavinliu.bus.station.db.Plan;
import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.utils.CalculateSoonBus;
import cn.gavinliu.bus.station.widget.BaseAdapter;
import cn.gavinliu.bus.station.widget.BaseListFragment;
import cn.gavinliu.bus.station.widget.BaseViewHolder;
import cn.gavinliu.zhuhai.station.R;
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

public class PlanDetailFragment extends BaseListFragment<Line, BaseViewHolder> {

    private static final String TAG = PlanDetailFragment.class.getSimpleName();

    public static final String KEY_PLAN = "KEY_PLAN";

    public static PlanDetailFragment newInstance(Plan plan) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_PLAN, plan);

        PlanDetailFragment fragment = new PlanDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private CompositeSubscription mSubscriptions;
    private Plan mPlan;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.clear();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mPlan != null) {
            updateBus(mPlan.getLines());
        }
    }

    @Override
    public void getArguments(Bundle bundle) {
        super.getArguments(bundle);
        if (bundle == null) return;
        mPlan = (Plan) bundle.getSerializable(KEY_PLAN);
    }

    private void updateBus(final List<Line> lines) {
        if (lines == null || lines.size() == 0) return;

        setListData(lines);

        Subscription subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Long aLong) {
                        return BusQueryServiceImpl.getInstance().updateBusForLine(lines.get((int) (aLong % lines.size())));
                    }
                })
                .delay(1, TimeUnit.SECONDS)
                .flatMap(new Func1<Line, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Line line) {
                        return BusQueryServiceImpl.getInstance().updateStationForLine(line);
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
                        e.printStackTrace();
                        Toast.makeText(getContext(), "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Line line) {
                        notifyDataSetChanged();
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    protected BaseAdapter createAdapter() {
        return new Adapter();
    }

    static class Adapter extends BaseAdapter<Line, Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_bus, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Line line = getItem(position);

            String distStr;
            if (line.getDist() > -1) {
                Bus soonBus = line.getSoonBus();
                distStr = holder.itemView.getResources()
                        .getString(R.string.bus_dist, soonBus.getBusNumber(), soonBus.getCurrentStation(), line.getDist());
            } else {
                distStr = holder.itemView.getResources().getString(R.string.bus_dist_tips);
            }

            String busStr;
            int busCount = line.getBuses() != null ? line.getBuses().size() : 0;
            if (busCount == 0) {
                busStr = holder.itemView.getResources().getString(R.string.bus_count_tips);
            } else {
                busStr = holder.itemView.getResources().getString(R.string.bus_count, busCount);
            }

            holder.mTitleText.setText(line.getName());
            holder.mBusText.setText(busStr);
            holder.mContentText.setText(distStr);
        }

    }

    static class Holder extends RecyclerView.ViewHolder {

        private TextView mTitleText;
        private TextView mBusText;
        private TextView mContentText;

        public Holder(View itemView) {
            super(itemView);
            mBusText = (TextView) itemView.findViewById(R.id.bus);
            mTitleText = (TextView) itemView.findViewById(R.id.title);
            mContentText = (TextView) itemView.findViewById(R.id.content);
        }
    }

}
