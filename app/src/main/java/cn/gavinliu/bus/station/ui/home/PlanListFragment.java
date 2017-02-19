package cn.gavinliu.bus.station.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.gavinliu.bus.station.db.Plan;
import cn.gavinliu.bus.station.ui.BaseListFragment;
import cn.gavinliu.bus.station.utils.ActivityRouter;
import cn.gavinliu.bus.station.utils.DbUtils;
import cn.gavinliu.bus.station.widget.BaseViewHolder;
import cn.gavinliu.zhuhai.station.R;

/**
 * Created by gavin on 2017/2/17.
 */

public class PlanListFragment extends BaseListFragment<Plan, BaseViewHolder> {

    private static final String TAG = PlanListFragment.class.getSimpleName();

    public static PlanListFragment newInstance() {
        Bundle args = new Bundle();

        PlanListFragment fragment = new PlanListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Plan> plans = DbUtils.getPlans();

        for (Plan plan : plans) {
            Log.d(TAG, plan.getName());
            Log.d(TAG, plan.getStation());
            Log.d(TAG, plan.getLines().get(0).getName());
        }

        setListData(plans);
    }

    private ItemListener mItemListener = new ItemListener() {

        @Override
        public void onItemClick(Plan plan) {
            ActivityRouter.startPlanDetail(getActivity(), plan);
        }
    };

    @Override
    protected BaseAdapter createAdapter() {
        return new Adapter(mItemListener);
    }

    private static class Adapter extends BaseListFragment.BaseAdapter<Plan, Holder> {

        private ItemListener mItemListener;

        public Adapter(ItemListener itemListener) {
            mItemListener = itemListener;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final Plan p = getItem(position);

            StringBuilder sb = new StringBuilder();

            sb.append("从 ");
            sb.append(p.getStation()).append(" ").append(p.getName());

            holder.mTextView.setText(sb.toString());
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onItemClick(p);
                }
            });
        }

    }

    private static class Holder extends BaseViewHolder {

        private TextView mTextView;

        public Holder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }
    }

    private interface ItemListener {
        void onItemClick(Plan plan);
    }
}
