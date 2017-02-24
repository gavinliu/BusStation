package cn.gavinliu.bus.station.utils;


import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Gavin on 17-2-23.
 */

public class EventCaster {

    private Bus bus;

    private static EventCaster sEvenEventcaster;

    public static synchronized EventCaster getInstance() {

        if (sEvenEventcaster == null) sEvenEventcaster = new EventCaster();
        return sEvenEventcaster;
    }

    private EventCaster() {
        bus = new Bus(ThreadEnforcer.ANY);
    }


    public void register(Object object) {
        bus.register(object);
    }

    public void unregister(Object object) {
        bus.unregister(object);
    }

    public void post(Object object) {
        bus.post(object);
    }

}
