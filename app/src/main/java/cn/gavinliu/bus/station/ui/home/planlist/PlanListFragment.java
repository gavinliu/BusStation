package cn.gavinliu.bus.station.ui.home.planlist;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.db.Plan;
import cn.gavinliu.bus.station.utils.ActivityRouter;
import cn.gavinliu.bus.station.utils.DbUtils;
import cn.gavinliu.bus.station.widget.BaseAdapter;
import cn.gavinliu.bus.station.widget.BaseListFragment;
import cn.gavinliu.bus.station.widget.BaseViewHolder;

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

    private Plan mTemp;
    private AlertDialog mDialog;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<Plan> plans = DbUtils.getPlans();
        setListData(plans);
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    private void createDialog() {
        mDialog = new AlertDialog.Builder(getContext())
                .setTitle("删除？")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbUtils.deletePlan(mTemp.getId());
                        setListData(DbUtils.getPlans());
                    }
                })
                .create();
    }

    @Override
    protected String getEmptyTipsText() {
        return getResources().getString(R.string.plan_list_empty_tips);
    }

    private ItemListener mItemListener = new ItemListener() {

        @Override
        public void onItemClick(Plan plan) {
            ActivityRouter.startPlanDetail(getActivity(), plan);
        }

        @Override
        public void onItemLongClick(Plan plan) {
            if (mDialog == null) createDialog();
            mTemp = plan;

            String msg = getString(R.string.plan_title, plan.getStation(), plan.getName());
            mDialog.setMessage(msg);
            mDialog.show();
        }
    };

    @Override
    protected BaseAdapter createAdapter() {
        return new Adapter(mItemListener);
    }

    private static class Adapter extends BaseAdapter<Plan, Holder> {

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

            String title = holder.mResources.getString(R.string.plan_title, p.getStation(), p.getName());
            String content = holder.mResources.getString(R.string.plan_content, p.getLines().size());

            holder.mTitleText.setText(title);
            holder.mContentText.setText(content);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onItemClick(p);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mItemListener.onItemLongClick(p);
                    return true;
                }
            });
        }

    }

    private static class Holder extends BaseViewHolder {

        private TextView mTitleText;
        private TextView mContentText;

        private Resources mResources;

        public Holder(View itemView) {
            super(itemView);
            mResources = itemView.getResources();

            mTitleText = (TextView) itemView.findViewById(R.id.title);
            mContentText = (TextView) itemView.findViewById(R.id.content);
        }
    }

    private interface ItemListener {
        void onItemClick(Plan plan);

        void onItemLongClick(Plan plan);
    }
}
