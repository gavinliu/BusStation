package cn.gavinliu.bus.station.ui.linedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.squareup.otto.Subscribe;

import java.util.List;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.entity.Bus;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.entity.Station;
import cn.gavinliu.bus.station.event.LineDetailUpdateEvent;
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
                String station = mAdapter.getStation();
                String busNumber = mAdapter.getBusNumber();

                if (busNumber == null || station == null) {
                    Toast.makeText(getContext(), "选择车辆和要提醒的站台", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (PermissionUtils.checkPermission(getActivity())) {
//                    MobclickAgent.onEvent(v.getContext(), "use_alarm");

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("到站提醒")
                            .setAction("到站提醒")
                            .setLabel("到站提醒")
                            .build());

                    AlarmManager.getInstance().setLineId(mLine.getId());
                    AlarmManager.getInstance().setBusNumber(busNumber);
                    AlarmManager.getInstance().setStationName(station);

                    Intent intent = new Intent(getActivity(), AlarmService.class);
                    intent.putExtra(AlarmService.KEY_ACTION, AlarmService.ACTION_LINE_ALARM);
                    intent.putExtra("LINE", mLine);
                    getActivity().startService(intent);

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

    @Override
    public String getPageName() {
        return "线路详情页";
    }

    @Subscribe
    public void updateBus(Line line) {
        if (line == null || !line.getId().equals(mLine.getId())) return;
        Log.d(TAG, "UpdateBus");
        mAdapter.setBuses(line.getBuses());

        if (mAdapter.getItemCount() == 0) {
            updateListData(line);
        }
    }

    @Subscribe
    public void onLineDetailUpdate(LineDetailUpdateEvent event) {
        startServiceLoad();

        if (mAdapter != null) {
            mAdapter.setStation(AlarmManager.getInstance().getStationName());
            mAdapter.setBusNumber(AlarmManager.getInstance().getBusNumber());
            mAdapter.notifyDataSetChanged();
        }
    }

    private void startServiceLoad() {
        Intent intent = new Intent(getActivity(), AlarmService.class);
        intent.putExtra(AlarmService.KEY_ACTION, AlarmService.ACTION_LINE_DETAIL);
        intent.putExtra("LINE", mLine);
        getActivity().startService(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startServiceLoad();

        updateListData(mLine);
    }

    private void updateListData(Line line) {
        if (line != null && line.getStations() != null) {
            setListData(line.getStations());
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
        mAdapter.setStation(AlarmManager.getInstance().getStationName());
        mAdapter.setBusNumber(AlarmManager.getInstance().getBusNumber());
        return mAdapter;
    }

    static class Adapter extends BaseAdapter<Station, Holder> implements View.OnClickListener {

        private List<Bus> mBuses;

        private String mStation;
        private String mBusNumber;

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

            if (mBuses != null) {
                int size = mBuses.size();
                int viewSize = holder.mBusContainer.getChildCount();

                for (int i = size - viewSize; i > 0; i--) {
                    TextView tv = new TextView(holder.itemView.getContext());
                    holder.mBusContainer.addView(tv);
                }

                int viewIndex = 0;
                for (int j = 0; j < size; j++) {
                    Bus bus = mBuses.get(j);
                    if (bus.getCurrentStation().equals(station.getName())) {
                        TextView tv = (TextView) holder.mBusContainer.getChildAt(viewIndex);

                        tv.setTag(bus);
                        tv.setText(bus.getBusNumber());
                        tv.setVisibility(View.VISIBLE);
                        tv.setOnClickListener(this);

                        if (bus.getBusNumber().equals(mBusNumber)) {
                            tv.setTextColor(holder.itemView.getResources().getColor(R.color.primary));
                        } else {
                            tv.setTextColor(holder.itemView.getResources().getColor(R.color.primary_text));
                        }
                        viewIndex++;
                    }
                }

                viewSize = holder.mBusContainer.getChildCount();
                for (int i = viewIndex; i < viewSize; i++) {
                    TextView tv = (TextView) holder.mBusContainer.getChildAt(i);
                    tv.setVisibility(View.GONE);
                }
            }

            holder.mAlarmBtn.setTag(station);
            holder.mAlarmBtn.setOnClickListener(this);

            if (station.getName().equals(mStation)) {
                holder.mAlarmBtn.setImageResource(R.mipmap.ic_alarm_select);
            } else {
                holder.mAlarmBtn.setImageResource(R.mipmap.ic_alarm_normal);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getTag() instanceof Bus) {
                mBusNumber = ((Bus) v.getTag()).getBusNumber();
            } else if (v.getTag() instanceof Station) {
                mStation = ((Station) v.getTag()).getName();
            }

            notifyDataSetChanged();
        }

        public String getStation() {
            return mStation;
        }

        public String getBusNumber() {
            return mBusNumber;
        }

        public void setStation(String station) {
            mStation = station;
        }

        public void setBusNumber(String busNumber) {
            mBusNumber = busNumber;
        }
    }

    static class Holder extends BaseViewHolder {

        private TextView mTitleText;
        private LinearLayout mBusContainer;
        private ImageButton mAlarmBtn;

        public Holder(View itemView) {
            super(itemView);
            mTitleText = (TextView) itemView.findViewById(R.id.title);
            mBusContainer = (LinearLayout) itemView.findViewById(R.id.bus_container);
            mAlarmBtn = (ImageButton) itemView.findViewById(R.id.alarm_btn);
        }
    }

}
