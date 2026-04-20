package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.UnitTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;

public class ActionLeaveTypePost extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(person)) {
                throw new ExceptionAccessDenied(person);
            }
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isBlank(wi.getName())) {
                throw new ExceptionEmptyParameter("假期名称");
            }
            // 名称不能重复
            List<AttendanceV2LeaveType> checkRepetitive = emc.listEqualAndEqual(
                    AttendanceV2LeaveType.class,
                    AttendanceV2LeaveType.name_FIELDNAME, wi.getName(),
                    AttendanceV2LeaveType.active_FIELDNAME, true);
            if (checkRepetitive != null && !checkRepetitive.isEmpty()) {
                for (AttendanceV2LeaveType check : checkRepetitive) {
                    if (check.getName().equals(wi.getName()) && !check.getId().equals(wi.getId())) {
                        throw new ExceptionWithMessage("假期名称已存在");
                    }
                }
            }
            if (StringUtils.isBlank(wi.getQuotaType()) || (
                    !QuotaTypeEnum.QUOTA.getValue().equals(wi.getQuotaType())
                    && !QuotaTypeEnum.UNLIMITED.getValue().equals(wi.getQuotaType()))) {
                throw new ExceptionEmptyParameter("额度类型");
            }
            if (StringUtils.isBlank(wi.getUnit()) || (
                    !UnitTypeEnum.DAY.getValue().equals(wi.getUnit())
                    && !UnitTypeEnum.HOUR.getValue().equals(wi.getUnit()))) {
                throw new ExceptionEmptyParameter("单位");
            }
            AttendanceV2LeaveType leaveType = Wi.copier.copy(wi);
            emc.beginTransaction(AttendanceV2LeaveType.class);
            if (StringUtils.isNotEmpty(wi.getId())) {
                AttendanceV2LeaveType old = emc.find(wi.getId(),
                        AttendanceV2LeaveType.class);
                if (old != null) {
                    leaveType.copyTo(old, JpaObject.FieldsUnmodify);
                    emc.check(old, CheckPersistType.all);
                    Wo wo = new Wo();
                    wo.setId(old.getId());
                    result.setData(wo);
                    emc.commit();
                    return result;
                }
            }
            Integer order = business.getAttendanceV2ManagerFactory()
                    .findAttendanceV2LeaveTypeBiggestOrderNumber();
            if (order == null) {
                order = 100;
            } else {
                order = order + 1;
            }
            leaveType.setOrderNumber(order);
            emc.persist(leaveType, CheckPersistType.all);
            Wo wo = new Wo();
            wo.setId(leaveType.getId());
            result.setData(wo);
            emc.commit();
            return result;
        }
    }

    public static class Wi extends AttendanceV2LeaveType {

        private static final long serialVersionUID = -2293772894673897906L;
        static WrapCopier<Wi, AttendanceV2LeaveType> copier = WrapCopierFactory.wi(Wi.class,
                AttendanceV2LeaveType.class, null,
                JpaObject.FieldsUnmodify);

    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = -409828970324495394L;
    }
}
