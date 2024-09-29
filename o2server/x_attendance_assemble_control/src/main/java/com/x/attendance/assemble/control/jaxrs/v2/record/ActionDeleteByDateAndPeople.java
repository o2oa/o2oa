package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by fancyLou on 2023/4/25.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionDeleteByDateAndPeople extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteByDateAndPeople.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, String peopleDn, String date) throws  Exception {

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            if (StringUtils.isEmpty(peopleDn)) {
                throw new ExceptionEmptyParameter("人员DN");
            }
            if (StringUtils.isEmpty(date)) {
                throw new ExceptionEmptyParameter("日期");
            }
            Date dateCheck = null;
            try {
                dateCheck = DateTools.parse(date, DateTools.format_yyyyMMdd); // 检查格式
            } catch (Exception ignore) {}
            if (dateCheck == null) {
                throw new ExceptionWithMessage("日期格式错误，需要格式：yyyy-MM-dd");
            }
            deleteOldRecords(peopleDn, date, emc, business);
            // 删除了旧的记录，重新生成
            LOGGER.info("发起考勤数据生成，Date：{} person: {}", date, peopleDn);
            ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(peopleDn, date));
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }


    public static class Wo extends WrapOutBoolean {
        private static final long serialVersionUID = -1822616682929319572L;
    }
}
