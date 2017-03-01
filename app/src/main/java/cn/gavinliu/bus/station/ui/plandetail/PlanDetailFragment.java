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

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.db.Plan;
import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.utils.ActivityRouter;
import cn.gavinliu.bus.station.utils.CalculateSoonBus;
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
    public void onResume() {
        super.onResume();
        updateBus();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSubscriptions.clear();
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    public void getArguments(Bundle bundle) {
        super.getArguments(bundle);
        if (bundle == null) return;
        mPlan = (Plan) bundle.getSerializable(KEY_PLAN);
    }

    private void updateBus() {
        if (mPlan == null) return;

        final List<Line> lines = mPlan.getLines();

        if (lines == null || lines.size() == 0) return;

        setListData(lines);

        Subscription subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Long aLong) {
                        return BusQueryServiceImpl.getInstance().updateStationForLine(lines.get((int) (aLong % lines.size())));

                    }
                })
                .delay(1, TimeUnit.SECONDS)
                .flatMap(new Func1<Line, Observable<Line>>() {
                    @Override
                    public Observable<Line> call(Line line) {
                        return BusQueryServiceImpl.getInstance().updateBusForLine(line);
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

    private ItemListener mItemListener = new ItemListener() {
        @Override
        public void onItemClick(Line line) {
            ActivityRouter.startLineDetail(getActivity(), line);
        }
    };

    @Override
    protected BaseAdapter createAdapter() {
        return new Adapter(mItemListener);
    }

    static class Adapter extends BaseAdapter<Line, Holder> {

        private ItemListener mItemListener;

        public Adapter(ItemListener itemListener) {
            mItemListener = itemListener;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_bus, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final Line line = getItem(position);

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

            String update;
            if (line.getUpdateTime() != null) {
                update = holder.itemView.getResources().getString(R.string.bus_update_time, line.getUpdateTime());
            } else {
                update = holder.itemView.getResources().getString(R.string.bus_update_tips);
            }

            holder.mBusText.setText(busStr);
            holder.mUpdateText.setText(update);
            holder.mContentText.setText(distStr);
            holder.mTitleText.setText(line.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onItemClick(line);
                }
            });
        }

    }

    static class Holder extends RecyclerView.ViewHolder {

        private TextView mTitleText;
        private TextView mBusText;
        private TextView mContentText;
        private TextView mUpdateText;

        public Holder(View itemView) {
            super(itemView);
            mBusText = (TextView) itemView.findViewById(R.id.bus);
            mTitleText = (TextView) itemView.findViewById(R.id.title);
            mContentText = (TextView) itemView.findViewById(R.id.content);
            mUpdateText = (TextView) itemView.findViewById(R.id.update);
        }
    }

    private interface ItemListener {
        void onItemClick(Line line);
    }

}
