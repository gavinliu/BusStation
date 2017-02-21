package cn.gavinliu.bus.station.ui.choiceline;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.db.Plan;
import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.utils.ActivityRouter;
import cn.gavinliu.bus.station.utils.DbUtils;
import cn.gavinliu.bus.station.widget.BaseAdapter;
import cn.gavinliu.bus.station.widget.BaseListFragment;
import cn.gavinliu.bus.station.widget.BaseViewHolder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gavin on 2017/2/19.
 */

public class ChoiceLineFragment extends BaseListFragment<Line, BaseViewHolder> {

    private static final String TAG = ChoiceLineFragment.class.getSimpleName();

    public static final String KEY_STATION = "KEY_STATION";

    public static ChoiceLineFragment newInstance(String station) {
        Bundle args = new Bundle();
        args.putString(KEY_STATION, station);

        ChoiceLineFragment fragment = new ChoiceLineFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private String mStation;

    private Adapter mAdapter;

    private AlertDialog mDialog;

    private Button mButton;

    private EditText mEditText;

    private ArrayList<Line> mSelectedLines;

    @Override
    protected int fragmentLayoutID() {
        return R.layout.fragment_choiceline;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButton = (Button) view.findViewById(R.id.start);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedLines = mAdapter.getSelectedLines();

                if (mDialog == null) createDialog();
                mDialog.show();
            }

        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLine(mStation);
    }

    @Override
    public void getArguments(Bundle bundle) {
        super.getArguments(bundle);
        if (bundle == null) return;

        mStation = bundle.getString(KEY_STATION);
    }

    private void createDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_save_plan, null, false);
        mEditText = (EditText) view.findViewById(R.id.edit);
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.plan_save)
                .setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string = mEditText.getText().toString();
                        if (!TextUtils.isEmpty(string) && mSelectedLines.size() > 0) {
                            Plan plan = new Plan();

                            plan.setName(string);
                            plan.setStation(mStation);
                            plan.setLines(mSelectedLines);

                            DbUtils.savePlan(plan);

                            ActivityRouter.startPlanDetail(getActivity(), plan);
                        } else {
                            Toast.makeText(getActivity(), TextUtils.isEmpty(string) ? R.string.plan_name_null_tips : R.string.plan_lines_null_tips, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create();
    }

    private void getLine(String key) {
        BusQueryServiceImpl.getInstance().getLineListByStationName(key)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Line>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showTipsView(getResources().getString(R.string.server_error));
                    }

                    @Override
                    public void onNext(List<Line> lines) {
                        for (Line line : lines) {
                            line.setCurrentStation(mStation);
                        }
                        setListData(lines);
                    }
                });
    }

    @Override
    protected void showRecyclerView() {
        super.showRecyclerView();
        mButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void showTipsView(String tips) {
        super.showTipsView(tips);
        mButton.setVisibility(View.GONE);
    }

    @Override
    protected void showLoadingView() {
        super.showLoadingView();
        mButton.setVisibility(View.GONE);
    }

    @Override
    protected BaseAdapter createAdapter() {
        mAdapter = new Adapter();
        return mAdapter;
    }

    static class Adapter extends BaseAdapter<Line, Holder> {

        private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choiceline, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, int position) {
            Line line = getItem(position);

            holder.itemView.setSelected(mSelectedPositions.get(position));
            holder.mCheckBox.setChecked(mSelectedPositions.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedPositions.put(holder.getAdapterPosition(), !holder.mCheckBox.isChecked());
                    notifyDataSetChanged();
                }
            });

            String content = holder.itemView.getResources().getString(R.string.line_line, line.getFromStation(), line.getToStation());

            holder.mTitleText.setText(line.getName());
            holder.mContentText.setText(content);
        }

        public ArrayList<Line> getSelectedLines() {
            ArrayList<Line> lines = new ArrayList<>();
            for (int i = 0; i < mSelectedPositions.size(); i++) {
                int key = mSelectedPositions.keyAt(i);
                if (mSelectedPositions.get(key)) {
                    lines.add(getItem(key));
                }
            }
            return lines;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        private CheckBox mCheckBox;
        private TextView mTitleText;
        private TextView mContentText;


        public Holder(View itemView) {
            super(itemView);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            mTitleText = (TextView) itemView.findViewById(R.id.text);
            mContentText = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
