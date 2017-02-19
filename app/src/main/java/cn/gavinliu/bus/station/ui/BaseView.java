package cn.gavinliu.bus.station.ui;

/**
 * Created by gavin on 2017/2/20.
 */

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

}