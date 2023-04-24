package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2DetailModel;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;
import com.x.general.core.entity.GeneralFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fancyLou on 2023/4/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionImportExcel extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionImportExcel.class);

    private static ReentrantLock lock = new ReentrantLock();

    ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
            throws Exception {
        lock.lock();
        LOGGER.info("开始导入打卡记录数据。。。。。。。。。。");
        try (InputStream is = new ByteArrayInputStream(bytes);
             XSSFWorkbook workbook = new XSSFWorkbook(is);
             ByteArrayOutputStream os = new ByteArrayOutputStream();
             EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Sheet sheet = workbook.getSheetAt(0); // 第一个sheet
            // 固定模版
            int firstRow = sheet.getFirstRowNum() + 1; // 第一行是标题跳过
            int lastRow = sheet.getLastRowNum();
            // count 一下 错误条数
            int errorRowNumber = 0;
            for (int i = firstRow; i <= lastRow; i++) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("处理行数：" + i + " ========================================");
                }
                Row row = sheet.getRow(i);
                String person = AttendanceV2Helper.getExcelCellStringValue(row.getCell(0)); // 第一条是person
                if (StringUtils.isEmpty(person)) {
                    setExcelCellError(row, "用户标识不能为空！");
                    errorRowNumber++;
                    continue;
                }
                Person p = business.organization().person().getObject(person);
                if (p == null || StringUtils.isEmpty(p.getDistinguishedName())) {
                    setExcelCellError(row, "用户查询不到！");
                    errorRowNumber++;
                    continue;
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("用户：" + p.getDistinguishedName());
                }
                try {
                    if (!importRecordWithDatePerRow(row, p.getDistinguishedName(), emc, business)) {
                        errorRowNumber++;
                    }
                } catch (Exception e) {
                    setExcelCellError(row, e.getLocalizedMessage());
                    errorRowNumber++;
                }
            }
            ActionResult<Wo> result = new ActionResult<>();
            // 存储 excel 文件
            String name = "attendance_record_data_input_" + DateTools.formatDate(new Date()) + ".xlsx";
            workbook.write(os);
            StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
            GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name,
                    effectivePerson.getDistinguishedName());
            generalFile.saveContent(gfMapping, os.toByteArray(), name);
            emc.beginTransaction(GeneralFile.class);
            emc.persist(generalFile, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setFlag(generalFile.getId());
            wo.setErrorRows(errorRowNumber);
            result.setData(wo);
            return result;
        } finally {
            lock.unlock();
            LOGGER.info("导入结束。。。。。。。。。。。。");
        }
    }


    /**
     * 当前一行数据导入处理
     * @param row
     * @param personDn
     * @param emc
     * @param business
     * @return
     * @throws Exception
     */
    private boolean importRecordWithDatePerRow(Row row, String personDn, EntityManagerContainer emc, Business business) throws Exception {
        // 打卡日期
        String date = AttendanceV2Helper.getExcelCellStringValue(row.getCell(1)); // 第二条是打卡日期  yyyy-MM-dd
        if (StringUtils.isEmpty(date)) {
            setExcelCellError(row, "日期不能为空");
            return false;
        }
        // 打卡日期
        Date recordDate = null;
        try {
            recordDate = DateTools.parse(date, DateTools.format_yyyyMMdd);
        } catch (Exception ignore) {}
        if (recordDate == null) {
            setExcelCellError(row, "日期格式不正确！");
            return false;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("打卡日期：" + date);
        }
        List<Date> recordList = new ArrayList<>();
        // 第2到第7列是打卡时间字段
        for (int i = 2; i < 8; i++) {
            String dutyTime = AttendanceV2Helper.getExcelCellStringValue(row.getCell(i)); // HH:mm
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
        List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory().listGroupWithPerson(personDn);
        if (groups == null || groups.isEmpty()) {
            setExcelCellError(row, "没有对应的考勤组");
            return false;
        }
        deleteOldRecords(personDn, date, emc, business); // 先删除老的
        AttendanceV2Group group = groups.get(0); // 考勤组
        // 固定班制
        if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Fixed)) {
            // 正常的班次id
            String shiftId = group.getWorkDateProperties().shiftIdWithDate(recordDate);
            // 是否特殊工作日
            if (StringUtils.isEmpty(shiftId)) {
                shiftId = AttendanceV2Helper.specialWorkDayShift(emc, date, group);
            }
            // 是否特殊节假日 清空shiftid
            if (StringUtils.isNotEmpty(shiftId) && AttendanceV2Helper.isSpecialRestDay(emc, date, group)) {
                shiftId = null;
            }
            if (StringUtils.isNotEmpty(shiftId)) {
                AttendanceV2Shift shift = emc.find(shiftId, AttendanceV2Shift.class);
                if (shift != null) { // 有班次对象
                    List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
                    if (timeList == null || timeList.isEmpty()) {
                        setExcelCellError(row, "没有对应的上下班打卡时间");
                        return false;
                    }
                    for (int i = 0; i < timeList.size(); i++) {
                        AttendanceV2ShiftCheckTime shiftCheckTime = timeList.get(i);
                        // 上班打卡
                        int recordIndex = i * 2;
                        Date onDutyRecordTime = recordList.get(recordIndex);
                        saveRecord(AttendanceV2CheckInRecord.OnDuty, personDn, date, onDutyRecordTime,
                                shiftCheckTime.getOnDutyTime(), shiftCheckTime.getOnDutyTimeBeforeLimit(), shiftCheckTime.getOnDutyTimeAfterLimit(),
                                group, shift, emc);
                        Date offDutyRecordTime = recordList.get(recordIndex + 1);
                        saveRecord(AttendanceV2CheckInRecord.OffDuty, personDn, date, offDutyRecordTime,
                                shiftCheckTime.getOffDutyTime(), shiftCheckTime.getOffDutyTimeBeforeLimit(), shiftCheckTime.getOffDutyTimeAfterLimit(),
                                group, shift, emc);
                    }
                }
            }
        } else if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Free)) {
            // 自由打卡 只读取两条数据
            Date onDutyRecordTime = recordList.get(0);
            saveRecord(AttendanceV2CheckInRecord.OnDuty, personDn, date, onDutyRecordTime,
                    null, null, null,
                    group, null, emc);
            Date offDutyRecordTime = recordList.get(1);
            saveRecord(AttendanceV2CheckInRecord.OffDuty, personDn, date, offDutyRecordTime,
                    null, null, null,
                    group, null, emc);
        } else {
             setExcelCellError(row, "考勤组考勤类型错误，未知的类型：" + group.getCheckType());
            return false;
        }
        LOGGER.info("导入数据成功，发起考勤数据生成，Date：{} person: {}", date, personDn);
        ThisApplication.queueV2Detail.send(new QueueAttendanceV2DetailModel(personDn, date));
        return true;
    }

    private void setExcelCellError(Row row, String error) {
        AttendanceV2Helper.setExcelCellError(row, error, 8);
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
    private void saveRecord(String dutyType, String person, String date, Date recordDate, String preDutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit,
                            AttendanceV2Group group, AttendanceV2Shift shift, EntityManagerContainer emc) throws Exception {
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setUserId(person);
        if (recordDate != null) {
            String checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
            if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Free)) {
                // 打卡时间
                if (StringUtils.isEmpty(preDutyTime)) {
                    if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                        preDutyTime = "09:00";
                    } else {
                        preDutyTime = "18:00";
                    }
                }
            } else {
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
            noCheckRecord.setCheckInResult(checkInResult);
        } else { // 没有打卡
            recordDate = DateTools.parse(date+" "+preDutyTime, DateTools.format_yyyyMMddHHmm);
            noCheckRecord.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned);
        }
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
    }

    /**
     * 先删除老的记录
     * @param personDn
     * @param date yyyy-MM-dd
     * @param emc
     * @param business
     * @throws Exception
     */
    private void deleteOldRecords(String personDn, String date, EntityManagerContainer emc, Business business) throws Exception {
        // 查询是否有存在的数据
        List<AttendanceV2CheckInRecord> oldRecordList = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(personDn, date);
        List<String> deleteIds = new ArrayList<>();
        for (AttendanceV2CheckInRecord record : oldRecordList) {
            deleteIds.add(record.getId());
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.delete(AttendanceV2CheckInRecord.class, deleteIds);
        emc.commit();
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("返回的结果标识，下载结果文件使用")
        private String flag;
        @FieldDescribe("异常错误数据条目数")
        private int errorRows;

        public int getErrorRows() {
            return errorRows;
        }

        public void setErrorRows(int errorRows) {
            this.errorRows = errorRows;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

    }
}
