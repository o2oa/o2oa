package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionRebuildDetailWithPersonDate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRebuildDetailWithPersonDate.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, String person, String date) throws Exception {
        if (StringUtils.isEmpty(person)) {
            throw new ExceptionEmptyParameter("person");
        }
        if (StringUtils.isEmpty(date)) {
            throw new ExceptionEmptyParameter("date");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
        }
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        if (AttendanceV2Helper.beforeToday(date)) {
            LOGGER.info("发起考勤数据生成，Date：{} person: {}", date, person);
            ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(person, date));
            wo.setValue(true);
        } else {
            wo.setValue(false);
        }
        result.setData(wo);
        return result;
    }

    public static class Wo extends WrapBoolean{

        private static final long serialVersionUID = -4587506309319949652L;
    }
}
