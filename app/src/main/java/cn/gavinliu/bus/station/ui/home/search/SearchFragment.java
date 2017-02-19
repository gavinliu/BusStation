package cn.gavinliu.bus.station.ui.home.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.widget.BaseFragment;
import cn.gavinliu.bus.station.ui.choiceline.ChoiceLineActivity;
import cn.gavinliu.zhuhai.station.R;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gavin on 2017/2/17.
 */

public class SearchFragment extends BaseFragment {

    private static final String TAG = SearchFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new Adapter();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setKeyword(String key) {
        BusQueryServiceImpl.getInstance().getStationNameList(key)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<String> strings) {
                        mAdapter.setData(strings);
                    }
                });
    }

    static class Adapter extends RecyclerView.Adapter<Holder> {

        private List<String> mData;

        public void setData(List<String> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            String item = mData.get(position);
            holder.setText(item);
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        private TextView text;

        public Holder(View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ChoiceLineActivity.class);
                    intent.putExtra(ChoiceLineActivity.KEY_STATION, text.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }

        public void setText(String text) {
            this.text.setText(text);
        }
    }
}
