package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateError;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fancyLou on 2023/4/28.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPostDailyRecord extends BaseAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionPostDailyRecord.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("用户标识");
            }
            Person p = business.organization().person().getObject(wi.getPerson());
            if (p == null || StringUtils.isEmpty(p.getDistinguishedName())) {
                throw new ExceptionNotExistObject("人员：" + wi.getPerson());
            }
            if (StringUtils.isEmpty(wi.getDate())) {
                throw new ExceptionEmptyParameter("日期");
            }
            // 打卡日期
            Date recordDate = null;
            try {
                recordDate = DateTools.parse(wi.getDate(), DateTools.format_yyyyMMdd);
            } catch (Exception ignore) {
            }
            if (recordDate == null) {
                throw new ExceptionWithMessage("日期格式不正确！");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("打卡日期：" + wi.getDate());
            }
            if (recordDate.after(new Date())) {
                throw new ExceptionWithMessage("不能导入未来的数据！");
            }

            List<Date> recordList = new ArrayList<>();
            addRecordTimes(wi.getDate(), wi.getOnDutyTime1(), recordList);
            addRecordTimes(wi.getDate(), wi.getOffDutyTime1(), recordList);
            addRecordTimes(wi.getDate(), wi.getOnDutyTime2(), recordList);
            addRecordTimes(wi.getDate(), wi.getOffDutyTime2(), recordList);
            addRecordTimes(wi.getDate(), wi.getOnDutyTime3(), recordList);
            addRecordTimes(wi.getDate(), wi.getOffDutyTime3(), recordList);
            // 考勤组和班次
            WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory()
                    .getGroupShiftByPersonDate(p.getDistinguishedName(), wi.getDate());
            if (woGroupShift == null || woGroupShift.getGroup() == null) {
                throw new ExceptionNotExistObject("没有对应的考勤组");
            }
            AttendanceV2Group group = woGroupShift.getGroup(); // 考勤组
            AttendanceV2Shift shift = woGroupShift.getShift(); // 班次
            // 固定班制 或者 排班制
            if ((group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Fixed)
                    || group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Arrangement)) && shift != null) {
                List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
                if (timeList == null || timeList.isEmpty()) {
                    throw new ExceptionWithMessage("没有对应的上下班打卡时间");
                }
                // 先删除老的
                deleteOldRecords(p.getDistinguishedName(), wi.getDate(), emc, business);
                for (int i = 0; i < timeList.size(); i++) {
                    AttendanceV2ShiftCheckTime shiftCheckTime = timeList.get(i);
                    // 上班打卡
                    int recordIndex = i * 2;
                    Date onDutyRecordTime = recordList.get(recordIndex);
                    saveRecord(AttendanceV2CheckInRecord.OnDuty, p.getDistinguishedName(), wi.getDate(),
                            onDutyRecordTime,
                            shiftCheckTime.getOnDutyTime(), shiftCheckTime.getOnDutyTimeBeforeLimit(),
                            shiftCheckTime.getOnDutyTimeAfterLimit(),
                            group, shift, emc);
                    Date offDutyRecordTime = recordList.get(recordIndex + 1);
                    saveRecord(AttendanceV2CheckInRecord.OffDuty, p.getDistinguishedName(), wi.getDate(),
                            offDutyRecordTime,
                            shiftCheckTime.getOffDutyTime(), shiftCheckTime.getOffDutyTimeBeforeLimit(),
                            shiftCheckTime.getOffDutyTimeAfterLimit(),
                            group, shift, emc);
                }
            } else {
                // 先删除老的
                deleteOldRecords(p.getDistinguishedName(), wi.getDate(), emc, business);
                // 自由打卡或者节假日没有班次的情况 只读取两条数据
                Date onDutyRecordTime = recordList.get(0);
                saveRecord(AttendanceV2CheckInRecord.OnDuty, p.getDistinguishedName(), wi.getDate(), onDutyRecordTime,
                        null, null, null,
                        group, null, emc);
                Date offDutyRecordTime = recordList.get(1);
                saveRecord(AttendanceV2CheckInRecord.OffDuty, p.getDistinguishedName(), wi.getDate(), offDutyRecordTime,
                        null, null, null,
                        group, null, emc);
            }
            LOGGER.info("导入数据成功，发起考勤数据生成，Date：{} person: {}", wi.getDate(), p.getDistinguishedName());
            ThisApplication.queueV2Detail
                    .send(new QueueAttendanceV2DetailModel(p.getDistinguishedName(), wi.getDate()));

            Wo wo = new Wo();
            wo.setValue(true);
            ActionResult<Wo> result = new ActionResult<>();
            result.setData(wo);
            return result;
        }
    }

    private void addRecordTimes(String date, String dutyTime, List<Date> recordList) {
        try {
            String timeStr = date + " " + dutyTime + ":00";
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("打卡时间：" + timeStr);
            }
            Date time = DateTools.parse(timeStr, DateTools.format_yyyyMMddHHmmss);
            recordList.add(time);
        } catch (Exception ignore) {
            recordList.add(null);
        }
    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = 7899934340364883697L;
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -8017286362885456659L;
        @FieldDescribe("用户唯一标识")
        private String person;
        @FieldDescribe("日期：yyyy-MM-dd")
        private String date;
        @FieldDescribe("第一次上班打卡时间：HH:mm")
        private String onDutyTime1;
        @FieldDescribe("第一次下班打卡时间：HH:mm")
        private String offDutyTime1;
        @FieldDescribe("第二次上班打卡时间：HH:mm")
        private String onDutyTime2;
        @FieldDescribe("第二次下班打卡时间：HH:mm")
        private String offDutyTime2;
        @FieldDescribe("第三次上班打卡时间：HH:mm")
        private String onDutyTime3;
        @FieldDescribe("第三次下班打卡时间：HH:mm")
        private String offDutyTime3;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getOnDutyTime1() {
            return onDutyTime1;
        }

        public void setOnDutyTime1(String onDutyTime1) {
            this.onDutyTime1 = onDutyTime1;
        }

        public String getOffDutyTime1() {
            return offDutyTime1;
        }

        public void setOffDutyTime1(String offDutyTime1) {
            this.offDutyTime1 = offDutyTime1;
        }

        public String getOnDutyTime2() {
            return onDutyTime2;
        }

        public void setOnDutyTime2(String onDutyTime2) {
            this.onDutyTime2 = onDutyTime2;
        }

        public String getOffDutyTime2() {
            return offDutyTime2;
        }

        public void setOffDutyTime2(String offDutyTime2) {
            this.offDutyTime2 = offDutyTime2;
        }

        public String getOnDutyTime3() {
            return onDutyTime3;
        }

        public void setOnDutyTime3(String onDutyTime3) {
            this.onDutyTime3 = onDutyTime3;
        }

        public String getOffDutyTime3() {
            return offDutyTime3;
        }

        public void setOffDutyTime3(String offDutyTime3) {
            this.offDutyTime3 = offDutyTime3;
        }
    }
}
