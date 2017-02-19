package cn.gavinliu.bus.station.widget;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by gavin on 2017/2/20.
 */

public abstract class BaseAdapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

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
