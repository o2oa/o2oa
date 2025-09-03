package com.x.query.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * @author chengjian
 * @date 2025/09/01 16:13
 **/
public class ItemAccessActivity extends JsonProperties {

    private static final long serialVersionUID = -2826601221045816580L;

    @FieldDescribe("活动ID.")
    private String activity;

    @FieldDescribe("活动名称.")
    private String activityName;

    @FieldDescribe("活动别名.")
    private String activityAlias;

    @FieldDescribe("活动类型.")
    private String activityType;

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityAlias() {
        return activityAlias;
    }

    public void setActivityAlias(String activityAlias) {
        this.activityAlias = activityAlias;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
}
