package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionCannotRepetitive;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTimeProperties;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

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
            Business business = new Business(emc);
            if(!business.isManager(effectivePerson)){
                throw new ExceptionAccessDenied(effectivePerson);
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isBlank(wi.getShiftName())) {
                throw new ExceptionEmptyParameter("班次名称");
            }
            // 名称不能相同
            List<AttendanceV2Shift> checkRepetitive = emc.listEqual(AttendanceV2Shift.class, AttendanceV2Shift.shiftName_FIELDNAME, wi.getShiftName());
            if (checkRepetitive != null && !checkRepetitive.isEmpty()) {
                for (AttendanceV2Shift check : checkRepetitive) {
                    if (check.getShiftName().equals(wi.getShiftName()) && !check.getId().equals(wi.getId())) {
                        throw new ExceptionCannotRepetitive("班次名称");
                    }
                }
            }
            AttendanceV2ShiftCheckTimeProperties properties = wi.getProperties();
            checkShiftProperties(properties);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("班次post {}", wi.toString());
            }
            long workTime = shiftWorkTime(properties);
            if (workTime < 0) {
                workTime = -workTime;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("班次工作 "+workTime);
            }
            // 修改
            AttendanceV2Shift shift = emc.find(wi.getId(), AttendanceV2Shift.class);
            emc.beginTransaction(AttendanceV2Shift.class);
            shift.setShiftName(wi.getShiftName());
            shift.setProperties(wi.getProperties());
            shift.setWorkTime((int)workTime);
            shift.setNeedLimitWorkTime(wi.getNeedLimitWorkTime());
            shift.setAbsenteeismLateMinutes(wi.getAbsenteeismLateMinutes());
            shift.setSeriousTardinessLateMinutes(wi.getSeriousTardinessLateMinutes());
            shift.setLateAndEarlyOffTime(wi.getLateAndEarlyOffTime());
            shift.setLateAndEarlyOnTime(wi.getLateAndEarlyOnTime());
            shift.setUpdateTime(new Date());
            shift.setOperator(effectivePerson.getDistinguishedName());
            emc.check(shift, CheckPersistType.all);
            emc.commit();
            CacheManager.notify(AttendanceV2Shift.class);// 清除缓存
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
