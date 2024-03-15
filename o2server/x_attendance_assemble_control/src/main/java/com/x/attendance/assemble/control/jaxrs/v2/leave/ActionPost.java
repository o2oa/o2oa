package com.x.attendance.assemble.control.jaxrs.v2.leave;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by fancyLou on 2023/3/29.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPost extends BaseAction {

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("人员标识");
            }
            if (StringUtils.isEmpty(wi.getLeaveType())) {
                throw new ExceptionEmptyParameter("请假类型");
            }
            if (null == wi.getStartTime()) {
                throw new ExceptionEmptyParameter("开始时间");
            }
            if (null == wi.getEndTime()) {
                throw new ExceptionEmptyParameter("结束时间");
            }
            // 人员查询 转化成DN
            Business business = new Business(emc);
            Person person = business.organization().person().getObject(wi.getPerson(), true);
            if (person == null) {
                throw new ExceptionNotExistObject("人员 " + wi.getPerson());
            }
            AttendanceV2LeaveData leaveData = Wi.copier.copy(wi);
            if (leaveData.getLeaveDayNumber() <= 0.0) { // 有传入数据 就不计算 按照传入的值来。
                // 计算日期间隔
                long interval = wi.getEndTime().getTime() - wi.getStartTime().getTime();
                if (interval < 0) {
                    interval = -interval;
                }
                double days = interval / (1000.0 * 3600 * 24);
                // 保留1位小数
                BigDecimal b = new BigDecimal(days);
                days = b.setScale(1, RoundingMode.HALF_UP).doubleValue();
                leaveData.setLeaveDayNumber(days);
            }
            leaveData.setPerson(person.getDistinguishedName());
            emc.beginTransaction(AttendanceV2LeaveData.class);
            emc.persist(leaveData, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wi extends AttendanceV2LeaveData {

        static WrapCopier<Wi, AttendanceV2LeaveData> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2LeaveData.class, null,
                JpaObject.FieldsUnmodify);
    }

    public static class Wo extends WrapBoolean {

    }
}
