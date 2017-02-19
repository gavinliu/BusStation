package cn.gavinliu.bus.station.utils;

import java.util.List;

import cn.gavinliu.bus.station.StationApplication;
import cn.gavinliu.bus.station.db.Plan;

/**
 * Created by gavin on 2017/2/19.
 */

public class DbUtils {

    public static void savePlan(Plan plan) {
        StationApplication.getLiteOrm().save(plan);
    }

    public static List<Plan> getPlans() {
        return StationApplication.getLiteOrm().query(Plan.class);
    }
}
