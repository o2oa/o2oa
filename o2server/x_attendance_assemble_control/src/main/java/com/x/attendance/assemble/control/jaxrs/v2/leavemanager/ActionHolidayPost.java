package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
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
import com.x.base.core.project.tools.DateTools;

public class ActionHolidayPost extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            AttendanceV2Holiday holiday = buildHoliday(wi);
            validateDateNotExists(emc.listEqual(AttendanceV2Holiday.class,
                    AttendanceV2Holiday.dateString_FIELDNAME, holiday.getDateString()));
            emc.beginTransaction(AttendanceV2Holiday.class);
            emc.persist(holiday, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setId(holiday.getId());
            result.setData(wo);
            return result;
        }
    }

    static AttendanceV2Holiday buildHoliday(Wi wi) throws Exception {
        if (StringUtils.isBlank(wi.getDateString()) || DateTools.parseDate(wi.getDateString()) == null) {
            throw new ExceptionWithMessage("日期不能为空，格式为 yyyy-MM-dd");
        }
        if (wi.getOffDay() == null) {
            throw new ExceptionEmptyParameter("是否放假日");
        }
        AttendanceV2Holiday holiday = Wi.copier.copy(wi);
        Date date = DateTools.parseDate(wi.getDateString());
        holiday.setYear(Integer.parseInt(DateTools.format(date, DateTools.format_yyyy)));
        holiday.setSource(AttendanceV2Holiday.SOURCE_API);
        return holiday;
    }

    static void validateDateNotExists(List<AttendanceV2Holiday> holidays) throws Exception {
        if (holidays != null && !holidays.isEmpty()) {
            throw new ExceptionWithMessage("当前日期的节假日数据已存在");
        }
    }

    public static class Wi extends AttendanceV2Holiday {

        private static final long serialVersionUID = 1259608805810239684L;
        static WrapCopier<Wi, AttendanceV2Holiday> copier = WrapCopierFactory.wi(Wi.class,
                AttendanceV2Holiday.class, null,
                JpaObject.FieldsUnmodify);
    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = 8005909775145288607L;
    }
}
