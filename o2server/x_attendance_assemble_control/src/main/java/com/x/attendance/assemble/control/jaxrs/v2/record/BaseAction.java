package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {


  private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    /**
     * 先删除老的记录
     * @param personDn
     * @param date yyyy-MM-dd
     * @param emc
     * @param business
     * @throws Exception
     */
    protected void deleteOldRecords(String personDn, String date, EntityManagerContainer emc, Business business) throws Exception {
        // 查询是否有存在的数据
        List<AttendanceV2CheckInRecord> oldRecordList = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(personDn, date);
        List<String> deleteIds = new ArrayList<>();
        for (AttendanceV2CheckInRecord record : oldRecordList) {
            deleteIds.add(record.getId());
            // 如果有异常数据也必须一起删除
            List<AttendanceV2AppealInfo> appealList = business.getAttendanceV2ManagerFactory().listAppealInfoWithRecordId(record.getId());
            if (appealList != null && !appealList.isEmpty()) {
                List<String> appealListDeleteIds = new ArrayList<>();
                for (AttendanceV2AppealInfo appealInfo : appealList) {
                    appealListDeleteIds.add(appealInfo.getId());
                }
                emc.beginTransaction(AttendanceV2AppealInfo.class);
                emc.delete(AttendanceV2AppealInfo.class, appealListDeleteIds);
                emc.commit();
            }
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.delete(AttendanceV2CheckInRecord.class, deleteIds);
        emc.commit();
        LOGGER.info("打卡记录删除：{}, {}, {} ", personDn, date, ListTools.toStringJoin(deleteIds));
    }


    /**
     * 保存打卡数据
     * @param dutyType
     * @param person
     * @param date
     * @param recordDate
     * @param preDutyTime
     * @param dutyTimeBeforeLimit
     * @param dutyTimeAfterLimit
     * @param group
     * @param shift
     * @param emc
     * @throws Exception
     */
    protected void saveRecord(String dutyType, String person, String date, Date recordDate, String preDutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit,
                            AttendanceV2Group group, AttendanceV2Shift shift, EntityManagerContainer emc) throws Exception {
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setUserId(person);
        // 打卡时间
        if (StringUtils.isEmpty(preDutyTime)) {
            if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                preDutyTime = "09:00";
            } else {
                preDutyTime = "18:00";
            }
        }
        String checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
        if (recordDate != null) {
            // 根据班次判断打卡结果
            if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Fixed) && shift != null) {
                // 上班打卡
                if (dutyType.equals(AttendanceV2CheckInRecord.OnDuty)) {
                    Date dutyTime = DateTools.parse(date + " " + preDutyTime, DateTools.format_yyyyMMddHHmm);
                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                    // 迟到
                    if (recordDate.after(dutyTime)) {
                        checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Late;
                    }
                    // 严重迟到
                    if (shift.getSeriousTardinessLateMinutes() > 0) {
                        Date seriousLateTime = DateTools.addMinutes(dutyTime, shift.getSeriousTardinessLateMinutes());
                        if (recordDate.after(seriousLateTime)) {
                            checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_SeriousLate;
                        }
                    }
                    // 可以晚到
                    if (StringUtils.isNotEmpty(shift.getLateAndEarlyOnTime())) {
                        int minute = -1;
                        try {
                            minute = Integer.parseInt(shift.getLateAndEarlyOnTime());
                        } catch (Exception ignored) {
                        }
                        if (minute > 0) {
                            Date lateAndEarlyOnTime = DateTools.addMinutes(dutyTime, minute);
                            if (recordDate.before(lateAndEarlyOnTime)) {
                                checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                            }
                        }
                    }
                } else if (dutyType.equals(AttendanceV2CheckInRecord.OffDuty)) {
                    Date offDutyTime = DateTools.parse(date + " " + preDutyTime, DateTools.format_yyyyMMddHHmm);
                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                    // 早退
                    if (recordDate.before(offDutyTime)) {
                        checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Early;
                    }
                    // 可以早走
                    if (StringUtils.isNotEmpty(shift.getLateAndEarlyOffTime())) {
                        int minute = -1;
                        try {
                            minute = Integer.parseInt(shift.getLateAndEarlyOffTime());
                        } catch (Exception e) {
                        }
                        if (minute > 0) {
                            Date lateAndEarlyOffTime = DateTools.addMinutes(offDutyTime, -minute);
                            if (recordDate.after(lateAndEarlyOffTime)) {
                                checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                            }
                        }
                    }

                }
            }
            
        } else { // 没有打卡
            recordDate = DateTools.parse(date+" "+preDutyTime, DateTools.format_yyyyMMddHHmm);
            checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned;
        }
        noCheckRecord.setCheckInResult(checkInResult);
        noCheckRecord.setRecordDate(recordDate);
        noCheckRecord.setRecordDateString(date);
        noCheckRecord.setPreDutyTime(preDutyTime);
        noCheckRecord.setPreDutyTimeBeforeLimit(dutyTimeBeforeLimit);
        noCheckRecord.setPreDutyTimeAfterLimit(dutyTimeAfterLimit);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_SYSTEM_IMPORT);
        noCheckRecord.setSourceDevice("其他");
        noCheckRecord.setDescription("");
        noCheckRecord.setGroupId(group.getId());
        noCheckRecord.setGroupName(group.getGroupName());
        noCheckRecord.setGroupCheckType(group.getCheckType());
        if (shift != null) {
            noCheckRecord.setShiftId(shift.getId());
            noCheckRecord.setShiftName(shift.getShiftName());
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(noCheckRecord, CheckPersistType.all);
        emc.commit();
        LOGGER.info("打卡记录保存：{}, {}, {} ", person, date, checkInResult);
    }

}
