package cn.gavinliu.bus.station.ui.linedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.List;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.entity.Station;
import cn.gavinliu.bus.station.service.AlarmManager;
import cn.gavinliu.bus.station.service.AlarmService;
import cn.gavinliu.bus.station.utils.EventCaster;
import cn.gavinliu.bus.station.utils.PermissionUtils;
import cn.gavinliu.bus.station.widget.BaseAdapter;
import cn.gavinliu.bus.station.widget.BaseListFragment;
import cn.gavinliu.bus.station.widget.BaseViewHolder;

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

    private Line mLine;
    private Adapter mAdapter;

    @Override
    protected int fragmentLayoutID() {
        return R.layout.fragment_linedetail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventCaster.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventCaster.getInstance().unregister(this);
        Intent intent = new Intent(getActivity(), AlarmService.class);
        intent.putExtra(AlarmService.KEY_ACTION, AlarmService.ACTION_DETAIL_FINISH);
        getActivity().startService(intent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.checkPermission(getActivity())) {
                    Intent intent = new Intent(getActivity(), AlarmService.class);
                    intent.putExtra(AlarmService.KEY_ACTION, AlarmService.ACTION_LINE_ALARM);
                    intent.putExtra("LINE", mLine);
                    getActivity().startService(intent);

                    AlarmManager.getInstance().setLineId(mLine.getId());
                    Log.d(TAG, AlarmManager.getInstance().getBusNumber() + " " + AlarmManager.getInstance().getStationName());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (PermissionUtils.checkPermission(getActivity())) {
                Toast.makeText(getContext(), "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "授权成功", Toast.LENGTH_SHORT).show();
            }
        }
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
        intent.putExtra(AlarmService.KEY_ACTION, AlarmService.ACTION_LINE_DETAIL);
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
            final Station station = getItem(position);
            holder.mTitleText.setText(station.getName());

            final StringBuilder sb = new StringBuilder();

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
            holder.mContentText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlarmManager.getInstance().setBusNumber(sb.toString());
                }
            });

            holder.mAlarmText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlarmManager.getInstance().setStationName(station.getName());
                }
            });
        }
    }

    static class Holder extends BaseViewHolder {

        private TextView mTitleText;
        private TextView mContentText;
        private TextView mAlarmText;

        public Holder(View itemView) {
            super(itemView);
            mTitleText = (TextView) itemView.findViewById(R.id.title);
            mContentText = (TextView) itemView.findViewById(R.id.content);
            mAlarmText = (TextView) itemView.findViewById(R.id.alarm);
        }
    }

}
