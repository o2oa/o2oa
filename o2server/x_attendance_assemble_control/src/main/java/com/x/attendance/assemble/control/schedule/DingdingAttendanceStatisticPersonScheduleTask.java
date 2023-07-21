package com.x.attendance.assemble.control.schedule;

import java.util.Date;

import org.quartz.JobExecutionContext;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;

/**
 * 钉钉考勤结果统计定时器
 * 每天晚上3点定时处理钉钉考勤的个人统计数据
 * Created by fancyLou on 2020-04-05.
 * Copyright © 2020 O2. All rights reserved.
 */
public class DingdingAttendanceStatisticPersonScheduleTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(DingdingAttendanceStatisticPersonScheduleTask.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            //前一天 0点到23点
            Date from = new Date();
            from = DateTools.addDay(from, -1);//前面一天
            ThisApplication.personStatisticQueue.send(from);
        }
    }



}
