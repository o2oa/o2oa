package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);


    protected class CallableImpl implements Callable<List<AttendanceV2CheckInRecord>> {

        private String person;
        private AttendanceV2Group group;
        private AttendanceV2Shift shift;
        private Date checkInDate;

        protected CallableImpl(String person, AttendanceV2Group group, AttendanceV2Shift shift,
                Date checkInDate) {
            this.person = person;
            this.group = group;
            this.shift = shift;
            this.checkInDate = checkInDate;
        }

        @Override
        public List<AttendanceV2CheckInRecord> call() throws Exception {
            // 查询打卡记录
            String today = DateTools.format(checkInDate, DateTools.format_yyyyMMdd);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("日期：{}", today);
            }
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                // 查询当前用户的考勤组
                Business business = new Business(emc);
                // 按照时间顺序查询出打卡列表
                List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                        .listRecordWithPersonAndDate(person, today);
                if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Arrangement)) {
                    // 排班制数据处理 排班制有可能有跨天的打卡 所以要先查询昨天的打卡记录
                    Date yesterday = DateTools.addDay(checkInDate, -1);
                    String yesterdayString = DateTools.format(yesterday, DateTools.format_yyyyMMdd);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("昨天日期：{}", yesterdayString);
                    }
                    List<AttendanceV2CheckInRecord> yesterdayRecordList = business.getAttendanceV2ManagerFactory()
                            .listRecordWithPersonAndDate(person, yesterdayString);
                    if (yesterdayRecordList == null || yesterdayRecordList.isEmpty()) {
                        if (shift != null) {
                            yesterdayRecordList = dealShiftForRecord(shift, emc, business, person,
                                    group, yesterday, yesterdayString, null);
                        }
                    }
                    if (yesterdayRecordList != null && !yesterdayRecordList.isEmpty()) {
                        AttendanceV2CheckInRecord last = yesterdayRecordList.get(
                                yesterdayRecordList.size() - 1);
                        // 昨天的数据 并且跨天
                        if (last.getOffDutyNextDay()) {
                            Date onDutyAfterTime;
                            if (StringUtils.isEmpty(last.getPreDutyTimeAfterLimit())) {
                                onDutyAfterTime = DateTools.parse(
                                        today + " " + last.getPreDutyTime(),
                                        DateTools.format_yyyyMMddHHmm);
                                onDutyAfterTime = DateTools.addMinutes(onDutyAfterTime,
                                        60);// 跨天的最后一条数据 超过 60 分钟
                            } else {
                                onDutyAfterTime = DateTools.parse(
                                        today + " " + last.getPreDutyTimeAfterLimit(),
                                        DateTools.format_yyyyMMddHHmm);
                            }
                            // 如果没有在限制时间结束前 就是返回昨天的打卡记录
                            if (!checkInDate.after(onDutyAfterTime)) {
                                LOGGER.info("返回昨日的数据，有跨天的还未完成的打卡");
                                return yesterdayRecordList;
                            }
                        }
                    }
                }
                if (shift != null) {
                    recordList = dealShiftForRecord(shift, emc, business, person, group,
                            checkInDate, today, recordList);
                }
                // 如果没有数据，可能是自由工时 或者 休息日没有班次信息的情况下 只需要生成一条上班一条下班的打卡记录
                if (recordList == null || recordList.isEmpty()) {
                    recordList = new ArrayList<>();
                    // 上班打卡
                    AttendanceV2CheckInRecord onDutyRecord = savePreCheckInRecord(emc, person,
                            AttendanceV2CheckInRecord.OnDuty, group, null, today,
                            null, null, null, false);
                    recordList.add(onDutyRecord);
                    // 下班打卡
                    AttendanceV2CheckInRecord offDutyRecord = savePreCheckInRecord(emc, person,
                            AttendanceV2CheckInRecord.OffDuty, group, null, today,
                            null, null, null, false);
                    recordList.add(offDutyRecord);
                }

                return recordList;
            }
        }

    }

    protected class CheckInCallableImpl implements Callable<AttendanceV2CheckInRecord> {

        private Date checkTime;
        private String recordId;
        private CheckInWi wi;

        protected CheckInCallableImpl(Date checkTime, String recordId, CheckInWi wi) {
            this.checkTime = checkTime;
            this.recordId = recordId;
            this.wi = wi;
        }

        @Override
        public AttendanceV2CheckInRecord call() throws Exception {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                AttendanceV2WorkPlace workPlace = null;
                if (StringUtils.isNotEmpty(wi.getWorkPlaceId())) {
                    workPlace = emc.find(wi.getWorkPlaceId(), AttendanceV2WorkPlace.class);
                }
                AttendanceV2CheckInRecord record = emc.find(recordId,
                        AttendanceV2CheckInRecord.class);
                if (record == null) {
                    throw new ExceptionNotExistObject("打卡记录");
                }
                String today = DateTools.format(checkTime, DateTools.format_yyyyMMdd);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("打卡日期：{}", today);
                }
                String checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                // 是否有班次信息
                if (StringUtils.isNotEmpty(record.getShiftId())) {
                    AttendanceV2Shift shift = business.getAttendanceV2ManagerFactory()
                            .pick(record.getShiftId(), AttendanceV2Shift.class);
                    if (shift == null) {
                        throw new ExceptionNotExistObject("班次对象");
                    }
                    if (StringUtils.isNotEmpty(record.getPreDutyTimeBeforeLimit())) {
                        Date beforeOnDuty = DateTools.parse(
                                today + " " + record.getPreDutyTimeBeforeLimit(),
                                DateTools.format_yyyyMMddHHmm);
                        if (checkTime.before(beforeOnDuty)) { // 不到开始时间不能打卡
                            throw new ExceptionTimeError("不到开始时间不能打卡");
                        }
                    }
                    if (StringUtils.isNotEmpty(record.getPreDutyTimeAfterLimit())) {
                        Date afterDuty = DateTools.parse(
                                today + " " + record.getPreDutyTimeAfterLimit(),
                                DateTools.format_yyyyMMddHHmm);
                        if (checkTime.after(afterDuty)) { // 超过结束时间不能打卡
                            throw new ExceptionTimeError("超过结束时间不能打卡");
                        }
                    }
                    // 上班打卡
                    if (record.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty)) {

                        Date dutyTime = DateTools.parse(today + " " + record.getPreDutyTime(),
                                DateTools.format_yyyyMMddHHmm);
                        checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                        // 迟到
                        if (checkTime.after(dutyTime)) {
                            checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Late;
                        }
                        // 严重迟到
                        if (shift.getSeriousTardinessLateMinutes() > 0) {
                            Date seriousLateTime = DateTools.addMinutes(dutyTime,
                                    shift.getSeriousTardinessLateMinutes());
                            if (checkTime.after(seriousLateTime)) {
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
                                if (checkTime.before(lateAndEarlyOnTime)) {
                                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                                }
                            }
                        }

                    } else if (record.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty)) {
                        Date offDutyTime = DateTools.parse(today + " " + record.getPreDutyTime(),
                                DateTools.format_yyyyMMddHHmm);
                        checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                        // 早退
                        if (checkTime.before(offDutyTime)) {
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
                                Date lateAndEarlyOffTime = DateTools.addMinutes(offDutyTime,
                                        -minute);
                                if (checkTime.after(lateAndEarlyOffTime)) {
                                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                                }
                            }
                        }
                        // 工作时长检查
                        if (checkInResult.equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL)
                            && BooleanUtils.isTrue(shift.getNeedLimitWorkTime())
                            && shift.getWorkTime() > 0) {
                            // 当前打卡的  recordString  查询对应的打卡记录，因为有可能跨天 需要查同一组打卡记录
                            List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                                    .listRecordWithPersonAndDate(record.getUserId(),
                                            record.getRecordDateString());
                            if (recordList == null || recordList.isEmpty()) {
                                throw new ExceptionNoTodayRecordList();
                            }
                            // 确定是最后一条打卡
                            if (record.getId()
                                    .equals(recordList.get(recordList.size() - 1).getId())) {
                                long realWorkTime = 0;
                                // 上班打卡
                                List<AttendanceV2CheckInRecord> onDutyList = recordList.stream()
                                        .filter(
                                                (r) -> r.getCheckInType()
                                                        .equals(AttendanceV2CheckInRecord.OnDuty))
                                        .sorted(Comparator.comparing(
                                                AttendanceV2CheckInRecord::getRecordDate))
                                        .collect(Collectors.toList());
                                // 下班打卡
                                List<AttendanceV2CheckInRecord> offDutyList = recordList.stream()
                                        .filter(
                                                (r) -> r.getCheckInType()
                                                        .equals(AttendanceV2CheckInRecord.OffDuty))
                                        .sorted(Comparator.comparing(
                                                AttendanceV2CheckInRecord::getRecordDate))
                                        .collect(Collectors.toList());
                                for (int i = 0; i < onDutyList.size(); i++) {
                                    AttendanceV2CheckInRecord onDuty = onDutyList.get(i);
                                    AttendanceV2CheckInRecord offDuty = offDutyList.get(i);
                                    if (offDuty.getId().equals(record.getId())) {
                                        realWorkTime += (checkTime.getTime()
                                                         - onDuty.getRecordDate().getTime());
                                    } else {
                                        realWorkTime += (offDuty.getRecordDate().getTime()
                                                         - onDuty.getRecordDate().getTime());
                                    }
                                }
                                if (realWorkTime < shift.getWorkTime()) { // 工作时长不足 标记未早退
                                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Early;
                                    LOGGER.info(
                                            "时长不足，标记为早退，person {} , realWorkTime {} , needWorkTime {}",
                                            record.getUserId(), "" + realWorkTime,
                                            "" + shift.getWorkTime());
                                }
                            }
                        }

                    }
                }

                // 更新打卡
                emc.beginTransaction(AttendanceV2CheckInRecord.class);
                record.setRecordDate(checkTime);
                record.setCheckInResult(checkInResult);
                if (workPlace != null) {
                    record.setWorkPlaceId(workPlace.getId());
                    record.setPlaceName(workPlace.getPlaceName());
                }

                if ("outside".equals(wi.getFrom())) {
                    record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_SYSTEM_IMPORT);
                    record.setSourceDevice(wi.getSource());
                    record.setDescription("");
                    record.setFieldWork(false);
                    record.setSignDescription("");
                    record.setLatitude("");
                    record.setLongitude("");
                    record.setRecordAddress("");
                } else if ("app".equals(wi.getFrom())) {
                    if (StringUtils.isNotEmpty(wi.getSourceType())) {
                        record.setSourceType(wi.getSourceType());
                    } else {
                        record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_USER_CHECK);
                    }
                    record.setSourceDevice(wi.getSourceDevice());
                    if (StringUtils.isNotEmpty(wi.getDescription())) {
                        record.setDescription(wi.getDescription());
                    } else {
                        record.setDescription("");
                    }
                    record.setFieldWork(wi.getFieldWork());
                    record.setSignDescription(wi.getSignDescription());
                    record.setLatitude(wi.getLatitude());
                    record.setLongitude(wi.getLongitude());
                    record.setRecordAddress(wi.getRecordAddress());
                }
                emc.check(record, CheckPersistType.all);
                emc.commit();
                LOGGER.info("checkIn 打卡 数据记录， 打卡人员：{}, 打卡日期：{}, 打卡结果：{} ",
                        record.getUserId(), today, checkInResult);
                return record;
            }
        }

    }


    /**
     * 异常
     *
     * @param record
     * @param emc
     * @param business
     */
    protected void generateAppealInfo(AttendanceV2CheckInRecord record, boolean fieldWorkMarkError,
            EntityManagerContainer emc, Business business) {
        try {
            if (record != null && record.checkResultException(fieldWorkMarkError)) {
                List<AttendanceV2AppealInfo> appealList = business.getAttendanceV2ManagerFactory()
                        .listAppealInfoWithRecordId(record.getId());
                if (appealList != null && !appealList.isEmpty()) {
                    LOGGER.info("当前打卡记录已经有申诉数据存在，不需要重复生成！{}", record.getId());
                    return;
                }
                AttendanceV2AppealInfo appealInfo = new AttendanceV2AppealInfo();
                appealInfo.setRecordId(record.getId());
                appealInfo.setRecordDateString(record.getRecordDateString());
                appealInfo.setRecordDate(record.getRecordDate());
                appealInfo.setUserId(record.getUserId());
                emc.beginTransaction(AttendanceV2AppealInfo.class);
                emc.persist(appealInfo, CheckPersistType.all);
                emc.commit();
                LOGGER.info("生成对应的异常打卡申请数据, {}", appealInfo.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }


    /**
     * 找出对应的打卡记录
     *
     * @param recordList   记录列表
     * @param currentIndex 第几次
     * @param checkType    OnDuty OffDuty
     * @return
     */
    protected AttendanceV2CheckInRecord hasCheckedRecord(List<AttendanceV2CheckInRecord> recordList,
            int currentIndex, String checkType) {
        if (recordList == null || recordList.isEmpty()) {
            return null;
        }
        List<AttendanceV2CheckInRecord> list = recordList.stream()
                .filter(r -> r.getCheckInType().equals(checkType)).collect(Collectors.toList());
        if (!list.isEmpty() && currentIndex <= list.size() - 1) {
            return list.get(currentIndex);
        }
        return null;
    }


    /**
     * 根据班次处理预存打卡记录
     *
     * @param shift
     * @param emc
     * @param person
     * @param group
     * @param nowDate
     * @param today
     * @param recordList
     * @throws Exception
     */
    private List<AttendanceV2CheckInRecord> dealShiftForRecord(AttendanceV2Shift shift,
            EntityManagerContainer emc,
            Business business,
            String person, AttendanceV2Group group, Date nowDate, String today,
            List<AttendanceV2CheckInRecord> recordList) throws Exception {
        List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
        if (timeList == null || timeList.isEmpty()) {
            LOGGER.info("没有对应的上下班打卡时间");
            // return null;
            throw new ExceptionEmptyParameter("没有对应的上下班打卡时间");
        }
        // 如果没有数据，先根据班次打卡信息 预存打卡数据
        if (recordList == null) {
            recordList = createNewRecordList(shift, emc, person, group, today, timeList);
        } else if (recordList.size() < (timeList.size() * 2)) {
            // 这里是为了处理异常数据，生成recordList的逻辑在上面，但是有可能有异常导致recordList数据保存不全，比如数据库异常
            List<AttendanceV2CheckInRecord> recordListNew = createNewRecordList(shift, emc, person, group, today, timeList);
            for (AttendanceV2CheckInRecord newRecord :  recordListNew) {
                AttendanceV2CheckInRecord oldOnDutyRecord = findRecordByExist(recordList,
                        newRecord.getCheckInType(), today,
                        newRecord.getPreDutyTime());
                if (oldOnDutyRecord != null) {
                    emc.beginTransaction(AttendanceV2CheckInRecord.class);
                    newRecord.setCheckInResult(oldOnDutyRecord.getCheckInResult());
                    newRecord.setRecordDate(oldOnDutyRecord.getRecordDate());
                    newRecord.setLatitude(oldOnDutyRecord.getLatitude());
                    newRecord.setLongitude(oldOnDutyRecord.getLongitude());
                    newRecord.setSourceDevice(oldOnDutyRecord.getSourceDevice());
                    newRecord.setSourceType(oldOnDutyRecord.getSourceType());
                    newRecord.setAppealId(oldOnDutyRecord.getAppealId());
                    newRecord.setRecordAddress(oldOnDutyRecord.getRecordAddress());
                    newRecord.setWorkPlaceId(oldOnDutyRecord.getWorkPlaceId());
                    newRecord.setPlaceName(oldOnDutyRecord.getPlaceName());
                    emc.persist(newRecord, CheckPersistType.all);
                    emc.commit();
                }
            }
            // 删除老的数据
            deleteOldRecordList(recordList, emc, business);
            // 自动处理 已经过来的打卡记录 记录为未打卡
            dealWithOvertimeRecord(emc, nowDate, today, recordListNew);
            return recordListNew;
        }
        // 自动处理 已经过来的打卡记录 记录为未打卡
        dealWithOvertimeRecord(emc, nowDate, today, recordList);
        return recordList;
    }

    private void deleteOldRecordList(List<AttendanceV2CheckInRecord> oldRecordList, EntityManagerContainer emc, Business business) throws Exception {
        List<String> deleteIds = new ArrayList<>();
        String personDn = "";
        String date = "";
        for (AttendanceV2CheckInRecord record : oldRecordList) {
            if (StringUtils.isEmpty(personDn)) {
                personDn = record.getUserId();
            }
            if (StringUtils.isEmpty(date)) {
                date = record.getRecordDateString();
            }
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

    private List<AttendanceV2CheckInRecord> createNewRecordList(AttendanceV2Shift shift,
            EntityManagerContainer emc, String person, AttendanceV2Group group, String today,
            List<AttendanceV2ShiftCheckTime> timeList) throws Exception {
        List<AttendanceV2CheckInRecord>  recordList = new ArrayList<>();
        for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
            // 上班打卡
            AttendanceV2CheckInRecord onDutyRecord = savePreCheckInRecord(emc, person,
                    AttendanceV2CheckInRecord.OnDuty, group, shift, today,
                    shiftCheckTime.getOnDutyTime(), shiftCheckTime.getOnDutyTimeBeforeLimit(),
                    shiftCheckTime.getOnDutyTimeAfterLimit(), false);
            recordList.add(onDutyRecord);
            // 下班打卡

            AttendanceV2CheckInRecord offDutyRecord = savePreCheckInRecord(emc, person,
                    AttendanceV2CheckInRecord.OffDuty, group, shift, today,
                    shiftCheckTime.getOffDutyTime(), shiftCheckTime.getOffDutyTimeBeforeLimit(),
                    shiftCheckTime.getOffDutyTimeAfterLimit(),
                    BooleanUtils.isTrue(shiftCheckTime.getOffDutyNextDay()));
            recordList.add(offDutyRecord);
        }
        return recordList;
    }

    /**
     * 查找已经存在的打卡记录
     */
    private AttendanceV2CheckInRecord findRecordByExist(List<AttendanceV2CheckInRecord> recordList,
            String dutyType, String today, String dutyTime) {
        // 打卡时间
        if (StringUtils.isEmpty(dutyTime)) {
            if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                dutyTime = "09:00";
            } else {
                dutyTime = "18:00";
            }
        }
        String finalDutyTime = dutyTime;
        return recordList.stream()
                .filter((r) -> r.getCheckInType().equals(dutyType) && r.getPreDutyTime().equals(
                        finalDutyTime) && r.getRecordDateString().equals(today))
                .findFirst().orElse(null);
    }

    /**
     * 处理超过时间的打卡记录 标记为未打卡
     *
     * @param emc
     * @param nowDate
     * @param today
     * @param recordList
     * @throws Exception
     */
    private void dealWithOvertimeRecord(EntityManagerContainer emc, Date nowDate, String today,
            List<AttendanceV2CheckInRecord> recordList) throws Exception {
        for (int i = 0; i < recordList.size(); i++) {
            AttendanceV2CheckInRecord record = recordList.get(i);
            // 不是预打卡数据 跳过
            if (record.getCheckInResult()
                    .equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)) {
                if (StringUtils.isNotEmpty(record.getPreDutyTimeAfterLimit())) { // 有打卡结束限制
                    Date onDutyAfterTime = DateTools.parse(
                            today + " " + record.getPreDutyTimeAfterLimit(),
                            DateTools.format_yyyyMMddHHmm);
                    if (nowDate.after(onDutyAfterTime)) { // 超过了打卡结束限制时间，直接生成未打卡数据
                        update2NoCheckInRecord(emc, record);
                    }
                } else {
                    Date onDutyTime = DateTools.parse(today + " " + record.getPreDutyTime(),
                            DateTools.format_yyyyMMddHHmm);
                    if (nowDate.after(onDutyTime)) {
                        // 查询下一条数据 下班打卡
                        if (i < recordList.size() - 1) {
                            AttendanceV2CheckInRecord nextRecord = recordList.get(i + 1);
                            Date offDutyTime = DateTools.parse(
                                    today + " " + nextRecord.getPreDutyTime(),
                                    DateTools.format_yyyyMMddHHmm);
                            if (nextRecord.getOffDutyNextDay()) { // 跨天的数据
                                offDutyTime = DateTools.addDay(offDutyTime, 1);
                            }
                            long minutes = (offDutyTime.getTime() - onDutyTime.getTime()) / 60000
                                           / 2; // 一半间隔时间
                            Date middleTime = DateTools.addMinutes(onDutyTime, (int) minutes);
                            if (nowDate.after(middleTime)) { // 生成未打卡数据
                                update2NoCheckInRecord(emc, record);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * 预存打卡数据保存
     *
     * @offDutyNextDay 是否是次日 最后一条下班打卡才有
     */
    private AttendanceV2CheckInRecord savePreCheckInRecord(EntityManagerContainer emc,
            String person, String dutyType,
            AttendanceV2Group group, AttendanceV2Shift shift, String today,
            String dutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit,
            boolean offDutyNextDay) throws Exception {
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn);
        noCheckRecord.setUserId(person);
        // 打卡时间
        if (StringUtils.isEmpty(dutyTime)) {
            if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                dutyTime = "09:00";
            } else {
                dutyTime = "18:00";
            }
        }
        Date onDutyTime = DateTools.parse(today + " " + dutyTime, DateTools.format_yyyyMMddHHmm);
        if (AttendanceV2CheckInRecord.OffDuty.equals(dutyType) && offDutyNextDay) {
            Date nextDate = DateTools.addDay(onDutyTime, 1);
            noCheckRecord.setRecordDate(nextDate);
        } else {
            noCheckRecord.setRecordDate(onDutyTime);
        }
        noCheckRecord.setRecordDateString(today);
        noCheckRecord.setPreDutyTime(dutyTime);
        noCheckRecord.setPreDutyTimeBeforeLimit(dutyTimeBeforeLimit);
        noCheckRecord.setPreDutyTimeAfterLimit(dutyTimeAfterLimit);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        noCheckRecord.setSourceDevice("其他");
        noCheckRecord.setDescription("系统生成，预打卡记录");
        noCheckRecord.setGroupId(group.getId());
        noCheckRecord.setGroupName(group.getGroupName());
        noCheckRecord.setGroupCheckType(group.getCheckType());
        noCheckRecord.setOffDutyNextDay(offDutyNextDay);
        if (shift != null) {
            noCheckRecord.setShiftId(shift.getId());
            noCheckRecord.setShiftName(shift.getShiftName());
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(noCheckRecord, CheckPersistType.all);
        emc.commit();
        LOGGER.info("打卡记录保存：{}, {}, {} ", person, today,
                AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn);
        return noCheckRecord;
    }

    /**
     * 更新为 未打卡
     *
     * @param emc
     * @param record
     * @throws Exception
     */
    private void update2NoCheckInRecord(EntityManagerContainer emc,
            AttendanceV2CheckInRecord record) throws Exception {
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned);
        record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        record.setSourceDevice("其他");
        record.setDescription("系统生成，缺卡记录");
        emc.persist(record, CheckPersistType.all);
        emc.commit();
    }


    public static class CheckInWi extends GsonPropertyObject {


        public static CheckInWi fromOutside(ActionCheckInRecordFromOut.Wi wi) {
            CheckInWi checkInWi = new CheckInWi();
            checkInWi.setFrom("outside");
            checkInWi.setPerson(wi.getPerson());
            checkInWi.setCheckInTime(wi.getCheckInTime());
            checkInWi.setSource(wi.getSource());
            checkInWi.setGenerateErrorInfo(wi.getGenerateErrorInfo());
            return checkInWi;
        }

        public static CheckInWi fromApp(ActionCheckIn.Wi wi) {
            CheckInWi checkInWi = new CheckInWi();
            checkInWi.setFrom("app");
            checkInWi.setRecordId(wi.getRecordId());
            checkInWi.setCheckInType(wi.getCheckInType());
            checkInWi.setWorkPlaceId(wi.getWorkPlaceId());
            checkInWi.setFieldWork(wi.getFieldWork());
            checkInWi.setSignDescription(wi.getSignDescription());
            checkInWi.setSourceDevice(wi.getSourceDevice());
            checkInWi.setDescription(wi.getDescription());
            checkInWi.setLongitude(wi.getLongitude());
            checkInWi.setLatitude(wi.getLatitude());
            checkInWi.setRecordAddress(wi.getRecordAddress());
            checkInWi.setSourceType(wi.getSourceType());
            return checkInWi;
        }

        @FieldDescribe("app|outside")
        private String from;


        // 外部来源字段
        @FieldDescribe("用户唯一标识")
        private String person;

        @FieldDescribe("打卡时间(Unix 时间戳)")
        private Long checkInTime;

        @FieldDescribe("来源， 比如门禁系统")
        private String source;

        @FieldDescribe("是否生成异常数据")
        private Boolean generateErrorInfo;


        // 移动端 打卡字段
        @FieldDescribe("打卡对象id")
        private String recordId;

        @FieldDescribe("打卡类型，OnDuty OffDuty")
        private String checkInType;

        @FieldDescribe("打卡工作场所id，范围内打卡需传入")
        private String workPlaceId;

        @FieldDescribe("是否外勤打卡.")
        private Boolean fieldWork;

        @FieldDescribe("外勤打卡说明")
        private String signDescription;

        @FieldDescribe("来源设备：Mac|Windows|IOS|Android|其他")
        private String sourceDevice;

        @FieldDescribe("其他说明备注")
        private String description;

        @FieldDescribe("当前位置经度")
        private String longitude;

        @FieldDescribe("当前位置纬度")
        private String latitude;

        @FieldDescribe("当前位置地点描述")
        private String recordAddress;

        @FieldDescribe("打卡数据来源： USER_CHECK（用户打卡） FAST_CHECK（极速打卡） ")
        private String sourceType;


        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public String getCheckInType() {
            return checkInType;
        }

        public void setCheckInType(String checkInType) {
            this.checkInType = checkInType;
        }

        public String getWorkPlaceId() {
            return workPlaceId;
        }

        public void setWorkPlaceId(String workPlaceId) {
            this.workPlaceId = workPlaceId;
        }

        public Boolean getFieldWork() {
            return fieldWork;
        }

        public void setFieldWork(Boolean fieldWork) {
            this.fieldWork = fieldWork;
        }

        public String getSignDescription() {
            return signDescription;
        }

        public void setSignDescription(String signDescription) {
            this.signDescription = signDescription;
        }

        public String getSourceDevice() {
            return sourceDevice;
        }

        public void setSourceDevice(String sourceDevice) {
            this.sourceDevice = sourceDevice;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getRecordAddress() {
            return recordAddress;
        }

        public void setRecordAddress(String recordAddress) {
            this.recordAddress = recordAddress;
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

        public Boolean getGenerateErrorInfo() {
            return generateErrorInfo;
        }

        public void setGenerateErrorInfo(Boolean generateErrorInfo) {
            this.generateErrorInfo = generateErrorInfo;
        }
    }
}
