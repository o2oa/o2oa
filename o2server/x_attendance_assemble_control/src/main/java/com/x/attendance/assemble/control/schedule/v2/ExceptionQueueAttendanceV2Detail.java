package com.x.attendance.assemble.control.schedule.v2;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ExceptionQueueAttendanceV2Detail extends PromptException {


    private static final long serialVersionUID = -2347831783819851010L;

    public ExceptionQueueAttendanceV2Detail(String message) {
        super(  "考勤V2处理考勤数据异常："+message );
    }
}
