package cn.gavinliu.bus.station.utils;

import android.app.Activity;
import android.content.Intent;

import cn.gavinliu.bus.station.db.Plan;
import cn.gavinliu.bus.station.ui.choiceline.ChoiceLineActivity;
import cn.gavinliu.bus.station.ui.plandetail.PlanDetailActivity;

/**
 * Created by gavin on 2017/2/19.
 */

public class ActivityRouter {

    public static void startPlanDetail(Activity activity, Plan plan) {
        Intent intent = new Intent(activity, PlanDetailActivity.class);
        intent.putExtra(PlanDetailActivity.KEY_PLAN, plan);
        activity.startActivity(intent);
    }

    public static void startLineList(Activity activity, String station) {
        Intent intent = new Intent(activity, ChoiceLineActivity.class);
        intent.putExtra(ChoiceLineActivity.KEY_STATION, station);
        activity.startActivity(intent);
    }

}
