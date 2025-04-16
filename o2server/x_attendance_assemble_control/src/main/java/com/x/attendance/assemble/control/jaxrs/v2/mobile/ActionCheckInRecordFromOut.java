package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 来自外部的打卡记录，比如门禁系统 有可能同一个人一天有很多条数据进入，需要判断更新哪条考勤记录
 */
public class ActionCheckInRecordFromOut extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheckInRecordFromOut.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("用户标识");
            }
            if (wi.getCheckInTime() == null) {
                throw new ExceptionEmptyParameter("打卡时间");
            }
            Person p = business.organization().person().getObject(wi.getPerson());
            if (p == null || StringUtils.isEmpty(p.getDistinguishedName())) {
                throw new ExceptionNotExistObject("人员：" + wi.getPerson());
            }
            Date checkInDate = null;
            try {
                checkInDate = new Date(wi.getCheckInTime()*1000);
            } catch (Exception ignore) {
            }
            if (checkInDate == null) {
                throw new ExceptionWithMessage("日期格式不正确！");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("打卡日期：" + checkInDate);
            }
            if (!isToday(checkInDate)) {
                throw new ExceptionWithMessage("打卡日期"+DateTools.format(checkInDate)+"不是今天！");
            }
            WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory()
                    .getGroupShiftByPersonDate(p.getDistinguishedName(),
                            DateTools.format(checkInDate, DateTools.format_yyyyMMdd));
            if (woGroupShift == null || woGroupShift.getGroup() == null) {
                throw new ExceptionNotExistObject("考勤组信息");
            }

            // 处理并发的问题
            List<AttendanceV2CheckInRecord> recordList = ThisApplication.executor
                    .submit(new CallableImpl(p.getDistinguishedName(), woGroupShift.getGroup(),
                            woGroupShift.getShift(), checkInDate)).get();
            if (recordList == null || recordList.isEmpty()) {
                throw new ExceptionNotExistObject("打卡记录");
            }
            AttendanceV2CheckInRecord record = recordList.stream()
                    .filter(r -> AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn.equals(
                            r.getCheckInResult())).findFirst().orElse(null);
            if (record == null) {
                record = recordList.get(recordList.size() - 1);
            }

            Wo wo = new Wo();
            ActionResult<Wo> result = new ActionResult<>();
            if (record != null) {
//                checkIn(emc, business, checkInDate, rInstance, null, null, wi);
                AttendanceV2CheckInRecord back = ThisApplication.checkInExecutor.submit(new CheckInCallableImpl(checkInDate, record.getId(), CheckInWi.fromOutside(wi))).get();
                if (BooleanUtils.isTrue(wi.getGenerateErrorInfo())) {
                    // 异常数据
                    generateAppealInfo(back, woGroupShift.getGroup().getFieldWorkMarkError(), emc,
                            business);
                }
                wo.setValue(true);
            } else {
                LOGGER.warn("没有找到打卡记录！");
                wo.setValue(false);
            }
            result.setData(wo);
            return result;
        }
    }

    private boolean isToday(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        return localDate.equals(today);
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -9108259407820975624L;
    }

    public static class Wi extends GsonPropertyObject {


        private static final long serialVersionUID = 3470655254449767419L;
        @FieldDescribe("用户唯一标识")
        private String person;
        @FieldDescribe("打卡时间(Unix 时间戳)")
        private Long checkInTime;
        @FieldDescribe("来源， 比如门禁系统")
        private String source;
        @FieldDescribe("是否生成异常数据")
        private Boolean generateErrorInfo;


        public Boolean getGenerateErrorInfo() {
            return generateErrorInfo;
        }

        public void setGenerateErrorInfo(Boolean generateErrorInfo) {
            this.generateErrorInfo = generateErrorInfo;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public Long getCheckInTime() {
            return checkInTime;
        }

        public void setCheckInTime(Long checkInTime) {
            this.checkInTime = checkInTime;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }
}
