package cn.gavinliu.bus.station.ui.linedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.List;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.entity.Station;
import cn.gavinliu.bus.station.service.AlarmService;
import cn.gavinliu.bus.station.utils.EventCaster;
import cn.gavinliu.bus.station.widget.BaseAdapter;
import cn.gavinliu.bus.station.widget.BaseListFragment;
import cn.gavinliu.bus.station.widget.BaseViewHolder;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by gavin on 2017/2/20.
 */

public class LineDetailFragment extends BaseListFragment<Station, BaseViewHolder> {

    private static final String TAG = LineDetailFragment.class.getSimpleName();

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
        EventCaster.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventCaster.getInstance().unregister(this);
        Intent intent = new Intent(getActivity(), AlarmService.class);
        getActivity().stopService(intent);
    }

    @Subscribe
    public void updateBus(Line line) {
        if (line == null) return;
        Log.d(TAG, "UpdateBus");
        mAdapter.setBuses(line.getBuses());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = new Intent(getActivity(), AlarmService.class);
        intent.putExtra("LINE", mLine);
        getActivity().startService(intent);

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
