package jiguang.chat.controller;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chenyn} on 2017/7/17.
 */

public class ActivityController {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
        activity.finish();
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
