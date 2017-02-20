package cn.gavinliu.bus.station.utils;

import com.litesuits.orm.db.assit.WhereBuilder;

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

    public static void deletePlan(int id) {
        StationApplication.getLiteOrm().delete(new WhereBuilder(Plan.class).where("_id = ?", id));
    }
}
