package cn.gavinliu.bus.station.ui.line;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.gavinliu.bus.station.entity.Line;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.ui.BaseActivity;
import cn.gavinliu.zhuhai.station.R;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gavin on 2017/2/17.
 */

public class LineListActivity extends BaseActivity {

    public static final String KEY_STATION = "KEY_STATION";

    private static final String TAG = LineListActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linelist);

        mAdapter = new Adapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        mButton = (Button) findViewById(R.id.start);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Line> lines = mAdapter.getSelectedLines();
                for (Line line : lines) {
                    Log.d(TAG, line.getName());
                }
            }
        });

        Intent intent = getIntent();

        if (intent != null) {
            String stationName = intent.getStringExtra(KEY_STATION);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(stationName);
            }
            getLine(stationName);
        }
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

                    }

                    @Override
                    public void onNext(List<Line> lines) {
                        mAdapter.setLines(lines);
                    }
                });
    }

    static class Adapter extends RecyclerView.Adapter<Holder> {

        private List<Line> mLines;
        private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

        public void setLines(List<Line> lines) {
            mLines = lines;
            notifyDataSetChanged();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, int position) {
            Line line = mLines.get(position);

            holder.mCheckBox.setChecked(mSelectedPositions.get(position));
            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedPositions.put(holder.getAdapterPosition(), ((CheckBox) v).isChecked());
                }
            });

            StringBuilder sb = new StringBuilder();
            sb.append(line.getName())
                    .append("\n")
                    .append(line.getFromStation())
                    .append(" -> ")
                    .append(line.getToStation());

            holder.mTextView.setText(sb);
        }

        public List<Line> getSelectedLines() {
            List<Line> lines = new ArrayList<>();
            for (int i = 0; i < mSelectedPositions.size(); i++) {
                int key = mSelectedPositions.keyAt(i);
                if (mSelectedPositions.get(key)) {
                    lines.add(mLines.get(key));
                }
            }
            return lines;
        }

        @Override
        public int getItemCount() {
            return mLines != null ? mLines.size() : 0;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        public CheckBox mCheckBox;
        public TextView mTextView;

        public Holder(View itemView) {
            super(itemView);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
