package cn.gavinliu.bus.station.ui.home.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.gavinliu.bus.station.R;
import cn.gavinliu.bus.station.network.BusQueryServiceImpl;
import cn.gavinliu.bus.station.ui.choiceline.ChoiceLineActivity;
import cn.gavinliu.bus.station.widget.BaseAdapter;
import cn.gavinliu.bus.station.widget.BaseListFragment;
import cn.gavinliu.bus.station.widget.BaseViewHolder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gavin on 2017/2/17.
 */

public class SearchFragment extends BaseListFragment<String, BaseViewHolder> {

    private static final String TAG = SearchFragment.class.getSimpleName();

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showTipsView("输入站台关键词，搜索相关站台");
    }

    @Override
    public String getPageName() {
        return "搜索页";
    }

    public void setKeyword(String key) {
        showLoadingView();

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
                        showTipsView(getResources().getString(R.string.server_error));
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        setListData(strings);
                    }
                });
    }

    @Override
    protected String getEmptyTipsText() {
        if (!isAdded()) {
            return "";
        }
        return getResources().getString(R.string.search_result_empty_hint);
    }

    @Override
    protected BaseAdapter createAdapter() {
        return new Adapter();
    }

    static class Adapter extends BaseAdapter<String, Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            String item = getItem(position);
            holder.setText(item);
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
