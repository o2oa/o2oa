package com.x.attendance.assemble.control.schedule.v2;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import org.quartz.JobExecutionContext;

/**
 * 新版考勤定时任务
 * 根据 打卡考勤记录AttendanceV2CheckInRecord 生成对应的 考勤详细数据AttendanceV2Detail
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2DetailGenerateTask  extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2DetailGenerateTask.class);


    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {

    }
}
