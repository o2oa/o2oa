package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

/**
 * 流程节点枚举类型 agent：脚本节点 begin：开始节点 cancel：取消节点 choice：选择活动节点 delay：定时活动节点
 * end：结束节点 embed：流程调用节点 invoke：服务调用节点 manual：人工节点 merge：合并活动节点 message：
 * parallel：并行活动节点 service：服务节点 split：拆分节点 publish：数据发布节点
 * 
 * @author sword
 */
public enum ActivityType {
    agent, begin, cancel, choice, delay, end, embed, invoke, manual, merge, parallel, publish, service, split,;

    public static final int length = JpaObject.length_16B;

    public static Class<? extends Activity> getClassOfActivityType(ActivityType activityType) {
        switch (activityType) {
            case agent:
                return Agent.class;
            case begin:
                return Begin.class;
            case cancel:
                return Cancel.class;
            case choice:
                return Choice.class;
            case delay:
                return Delay.class;
            case end:
                return End.class;
            case embed:
                return Embed.class;
            case invoke:
                return Invoke.class;
            case manual:
                return Manual.class;
            case merge:
                return Merge.class;
            case parallel:
                return Parallel.class;
            case publish:
                return Publish.class;
            case service:
                return Service.class;
            case split:
                return Split.class;
            default:
                return null;
        }
    }
}
