package com.x.attendance.assemble.control.jaxrs.v2.leave;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
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
import org.apache.commons.lang3.StringUtils;

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
            AttendanceV2LeaveData leaveData = Wi.copier.copy(wi);
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

        static WrapCopier< Wi, AttendanceV2LeaveData> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2LeaveData.class, null,
                JpaObject.FieldsUnmodify);
    }

    public static class Wo extends WrapBoolean {

    }
}
