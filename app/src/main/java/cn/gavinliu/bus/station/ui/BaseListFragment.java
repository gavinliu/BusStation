package cn.gavinliu.bus.station.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.gavinliu.zhuhai.station.R;

/**
 * Created by gavin on 2017/2/18.
 */

public abstract class BaseListFragment<E, VH extends RecyclerView.ViewHolder> extends BaseFragment {

    protected View mLoadingView;
    protected TextView mTipsView;
    protected RecyclerView mRecyclerView;
    protected BaseAdapter<E, VH> mAdapter;

    protected int createViewID() {
        return R.layout.base_fragment_list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(createViewID(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mTipsView = (TextView) view.findViewById(R.id.tips_view);
        mLoadingView = view.findViewById(R.id.loading_view);

        mAdapter = createAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLoadingView();
    }

    protected void showRecyclerView() {
        mLoadingView.setVisibility(View.GONE);
        mTipsView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    protected void showTipsView(String tips) {
        mLoadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        mTipsView.setVisibility(View.VISIBLE);
        mTipsView.setText(tips);
    }

    protected void showLoadingView() {
        mTipsView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    protected void setListData(List<E> data) {
        if (mAdapter == null) return;

        if (data != null &&data.size() > 0) {
            showRecyclerView();

            mAdapter.setData(data);
            mAdapter.notifyDataSetChanged();
        } else {
            showTipsView("没有");
        }

    }

    protected void notifyDataSetChanged() {
        if (mAdapter == null) return;
        mAdapter.notifyDataSetChanged();
    }

    protected abstract BaseAdapter<E, VH> createAdapter();

    protected static abstract class BaseAdapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

        private List<E> mData;

        public void setData(List<E> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }

        public E getItem(int position) {
            return mData.get(position);
        }
    }

}
