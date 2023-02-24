package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionRebuildDetailWithPersonDate extends BaseAction {

    ActionResult<Wo> execute(String person, String date) throws Exception {
        if (StringUtils.isEmpty(person)) {
            throw new ExceptionEmptyParameter("person");
        }
        if (StringUtils.isEmpty(date)) {
            throw new ExceptionEmptyParameter("date");
        }
        DateTools.parse(date, DateTools.format_yyyyMMdd); // 检查格式

        ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(person, date));
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setValue(true);
        result.setData(wo);
        return result;
    }

    public static class Wo extends WrapBoolean{

        private static final long serialVersionUID = -4587506309319949652L;
    }
}
