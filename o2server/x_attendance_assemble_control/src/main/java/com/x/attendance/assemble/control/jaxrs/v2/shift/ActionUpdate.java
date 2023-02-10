package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTimeProperties;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by fancyLou on 2023/1/31.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionUpdate extends BaseAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson,
                             JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isBlank(wi.getShiftName())) {
                throw new ExceptionEmptyParameter("班次名称");
            }
            AttendanceV2ShiftCheckTimeProperties properties = wi.getProperties();
            if (properties == null || properties.getTimeList() == null || properties.getTimeList().isEmpty()) {
                throw new ExceptionEmptyParameter("班次上下班打卡时间");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("班次post {}", wi.toString());
            }
            // 修改
            AttendanceV2Shift shift = emc.find(wi.getId(), AttendanceV2Shift.class);
            emc.beginTransaction(AttendanceV2Shift.class);
            shift.setShiftName(wi.getShiftName());
            shift.setProperties(wi.getProperties());
            shift.setAbsenteeismLateMinutes(wi.getAbsenteeismLateMinutes());
            shift.setSeriousTardinessLateMinutes(wi.getSeriousTardinessLateMinutes());
            shift.setLateAndEarlyOffTime(wi.getLateAndEarlyOffTime());
            shift.setLateAndEarlyOnTime(wi.getLateAndEarlyOnTime());
            shift.setUpdateTime(new Date());
            shift.setOperator(effectivePerson.getDistinguishedName());
            emc.check(shift, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setId(shift.getId());
            result.setData(wo);

        }
        return result;
    }


    public static class Wi extends AttendanceV2Shift {


        private static final long serialVersionUID = 2646507888175074720L;
        static WrapCopier<Wi, AttendanceV2Shift> copier = WrapCopierFactory.wi(Wi.class, AttendanceV2Shift.class, null,
                JpaObject.FieldsUnmodify);
    }

    public static class Wo extends WoId {


        private static final long serialVersionUID = -8022484130040762369L;
    }
}
