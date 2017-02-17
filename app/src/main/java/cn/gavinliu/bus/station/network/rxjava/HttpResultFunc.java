package cn.gavinliu.bus.station.network.rxjava;

import cn.gavinliu.bus.station.network.bean.ResponseBean;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Gavin on 17-1-6.
 */

public class HttpResultFunc<T> implements Func1<ResponseBean<T>, Observable<T>> {

    @Override
    public Observable<T> call(ResponseBean<T> responseBean) {
        return Observable.just(responseBean.data);
    }
}
