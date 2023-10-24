package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateError;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 新版考勤 部分共用业务逻辑工具类
 * Created by fancyLou on 2023/4/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2Helper {

    /**
     * 是否是今天之前的日期
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public static boolean beforeToday(String date) throws Exception {
        Date dateD = DateTools.parse(date, DateTools.format_yyyyMMdd); // 检查格式
        Date today = new Date();
        today = DateUtils.truncate(today, Calendar.DATE); // 今天 0 点 0 分
        if (dateD.after(today) || dateD.equals(today)) {
            throw new ExceptionDateError();
        }
        return true;
    }

    /**
     * 字符串是不是 月份格式yyyy-MM
     * 
     * @param dateString
     * @return
     */
    public static boolean isValidMonthString(String dateString) {
        // 正则表达式用于检查格式是否匹配
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}");
        Matcher matcher = pattern.matcher(dateString);
        if (!matcher.matches()) {
            return false; // 格式不匹配
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DateTools.format_yyyyMM);
        sdf.setLenient(false);
        try {
            // 使用SimpleDateFormat尝试解析日期
            sdf.parse(dateString);
            return true; // 解析成功，日期格式正确
        } catch (ParseException e) {
            return false; // 解析失败，日期格式不正确
        }
    }

    /**
     * 字符串是不是 日期格式 yyyy-MM-dd
     * 
     * @param dateString
     * @return
     */
    public static boolean isValidDateString(String dateString) {
        // 正则表达式用于检查格式是否匹配
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(dateString);
        if (!matcher.matches()) {
            return false; // 格式不匹配
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DateTools.format_yyyyMMdd);
        sdf.setLenient(false);
        try {
            // 使用SimpleDateFormat尝试解析日期
            sdf.parse(dateString);
            return true; // 解析成功，日期格式正确
        } catch (ParseException e) {
            return false; // 解析失败，日期格式不正确
        }
    }

    /**
     * 当前打卡对象是否是属于出勤
     *
     * != CHECKIN_RESULT_NotSigned !=CHECKIN_RESULT_PreCheckIn 没有请假数据
     * 
     * @param r 打卡对象
     * @return
     */
    public static boolean isRecordAttendance(AttendanceV2CheckInRecord r) {
        return (!r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned)
                && !r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn))
                || StringUtils.isNotEmpty(r.getLeaveDataId());
    }

    /**
     * 当前打卡对象是否属于未打卡数据
     *
     * result = CHECKIN_RESULT_NotSigned 并且 没有请假数据
     * 
     * @param r
     * @return
     */
    public static boolean isRecordNotSign(AttendanceV2CheckInRecord r) {
        return r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned)
                && StringUtils.isEmpty(r.getLeaveDataId());
    }

    /**
     * 处理考勤组 考勤人员 将人员、组织全部换成人员DN
     * 
     * @param emc
     * @param business
     * @param groupId
     * @param participateList
     * @param unParticipateList
     * @return
     * @throws Exception
     */
    public static List<String> calTruePersonFromMixList(EntityManagerContainer emc, Business business, String groupId,
            List<String> participateList, List<String> unParticipateList) throws Exception {
        // 处理考勤组
        List<String> peopleList = new ArrayList<>();
        for (String p : participateList) {
            if (p.endsWith("@P")) {
                peopleList.add(p);
            } else if (p.endsWith("@I")) {
                String person = business.organization().person().getWithIdentity(p);
                peopleList.add(person);
            } else if (p.endsWith("@U")) { // 递归查询人员
                List<String> pList = business.organization().person().listWithUnitSubNested(p);
                peopleList.addAll(pList);
            } else {
                // LOGGER.info("错误的标识？ " + p);
            }
        }
        // 删除排除的人员
        if (unParticipateList != null && !unParticipateList.isEmpty()) {
            for (String p : unParticipateList) {
                peopleList.remove(p);
            }
        }
        // 去重复
        HashSet<String> peopleSet = new HashSet<>(peopleList);
        // 判断是否和其它考勤组内的成员冲突
        List<String> conflictPersonInOtherGroup = new ArrayList<>();
        List<AttendanceV2Group> groups = emc.listAll(AttendanceV2Group.class);
        if (groups != null && !groups.isEmpty()) {
            for (String person : peopleSet) {
                for (AttendanceV2Group oldG : groups) {
                    // 自己不用处理
                    if (oldG.getId().equals(groupId)) {
                        continue;
                    }
                    if (oldG.getTrueParticipantList().contains(person)) {
                        conflictPersonInOtherGroup.add(person);
                        break;
                    }
                }
            }
        }
        if (!conflictPersonInOtherGroup.isEmpty()) {
            throw new ExceptionParticipateConflict(conflictPersonInOtherGroup);
        }
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("最终考勤组人员数：" + peopleSet.size());
        // }

        return new ArrayList<>(peopleSet);
    }

    /**
     * 是否特殊节假日
     * 
     * @param date  yyyy-MM-dd
     * @param group
     * @return
     * @throws Exception
     */
    public static boolean isSpecialRestDay(String date, AttendanceV2Group group) throws Exception {
        boolean isRestDay = false;
        // 考勤配置 节假日工作日
        Date myDate = DateTools.parse(date, DateTools.format_yyyyMMdd);
        if (Config.workTime() != null && Config.workTime().inDefinedHoliday(myDate)) {
            isRestDay = true;
        }
        // 考勤组的无需打卡日
        if (group.getNoNeedCheckInDateList() != null && !group.getNoNeedCheckInDateList().isEmpty()) {
            for (String d : group.getNoNeedCheckInDateList()) { // 包含日期 ｜ 是否循环
                String[] dArray = d.split("\\|");
                if (dArray.length < 2) {
                    // 格式不正确
                    continue;
                }
                if (dArray[0].equals(date)) {
                    isRestDay = true;// 无需打卡就是休息日
                    break;
                }
                if (dArray[1].equals("week")
                        && DateTools.dateIsInWeekCycle(DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                                DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 每周
                    isRestDay = true;// 无需打卡就是休息日
                    break;
                }
                if (dArray[1].equals("twoWeek")
                        && DateTools.dateIsInTwoWeekCycle(DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                                DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 每周
                    isRestDay = true;// 无需打卡就是休息日
                    break;
                }
                if (dArray[1].equals("month")
                        && DateTools.dateIsInMonthCycle(DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                                DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 每周
                    isRestDay = true;// 无需打卡就是休息日
                    break;
                }
            }

        }
        return isRestDay;
    }

    /**
     * 是否特殊工作日 并返回工作日的班次id
     * 
     * @param date  yyyy-MM-dd
     * @param group
     * @return
     * @throws Exception
     */
    public static String specialWorkDayShift(String date, AttendanceV2Group group) throws Exception {
        String shiftId = null;
        // 工作日
        Date myDate = DateTools.parse(date, DateTools.format_yyyyMMdd);
        if (Config.workTime() != null && Config.workTime().inDefinedWorkday(myDate)) {
            shiftId = group.getShiftId();
        }
        // 考勤组的必须打卡日
        if (group.getRequiredCheckInDateList() != null && !group.getRequiredCheckInDateList().isEmpty()) {
            for (String d : group.getRequiredCheckInDateList()) { // 包含日期 ｜ 班次id ｜ 是否循环
                String[] dArray = d.split("\\|");
                if (dArray.length < 3) {
                    // 格式不正确
                    continue;
                }
                if (dArray[0].equals(date)) {
                    shiftId = dArray[1];
                    break;
                }
                if (dArray[2].equals("week")
                        && DateTools.dateIsInWeekCycle(DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                                DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 每周
                    shiftId = dArray[1];
                    break;
                }
                if (dArray[2].equals("twoWeek")
                        && DateTools.dateIsInTwoWeekCycle(DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                                DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 每周
                    shiftId = dArray[1];
                    break;
                }
                if (dArray[2].equals("month")
                        && DateTools.dateIsInMonthCycle(DateTools.parse(dArray[0], DateTools.format_yyyyMMdd),
                                DateTools.parse(date, DateTools.format_yyyyMMdd))) { // 每周
                    shiftId = dArray[1];
                    break;
                }
            }
        }
        return shiftId;
    }

    public static void setExcelCellError(Row row, String error, int columnIndex) {
        Cell cell = CellUtil.getCell(row, columnIndex);
        cell.setCellValue(error);
    }

    public static String getExcelCellStringValue(Cell cell) {
        if (null != cell) {
            switch (cell.getCellType()) {
                case BLANK:
                    return "";
                case BOOLEAN:
                    return BooleanUtils.toString(cell.getBooleanCellValue(), "true", "false", "false");
                case ERROR:
                    return "";
                case FORMULA:
                    return "";
                case NUMERIC:
                    Double d = cell.getNumericCellValue();
                    Long l = d.longValue();
                    if (l.doubleValue() == d) {
                        return l.toString();
                    } else {
                        return d.toString();
                    }
                default:
                    return cell.getStringCellValue().trim();
            }
        }
        return "";
    }

    public static String getExcelCellDateFormat(Cell cell, String format) throws Exception {
        if (null == cell) {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            if (date != null) {
                return DateTools.format(date, format);
            }
        }
        String dateString = cell.getStringCellValue().trim();
        if (StringUtils.isEmpty(dateString)) {
            return "";
        }
        Date date = parseDate(dateString);
        if (date != null) {
            return DateTools.format(date, format);
        }
        return dateString;
    }

    private static Date parseDate(String dateString) throws ParseException {
        return DateUtils.parseDate(dateString,
                new String[] { DateTools.format_yyyyMMdd, DateTools.format_yyyyMMddHHmmss, DateTools.format_HHmmss, DateTools.format_HHmm, DateTools.format_yyyyMMddHHmm });
    }
}
