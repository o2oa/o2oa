package com.x.attendance.assemble.control.factory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDingtalkDetail;
import com.x.attendance.entity.AttendanceDingtalkDetail_;
import com.x.attendance.entity.AttendanceQywxDetail;
import com.x.attendance.entity.AttendanceQywxDetail_;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.attendance.entity.DingdingQywxSyncRecord_;
import com.x.attendance.entity.StatisticDingdingPersonForMonth;
import com.x.attendance.entity.StatisticDingdingPersonForMonth_;
import com.x.attendance.entity.StatisticDingdingUnitForDay;
import com.x.attendance.entity.StatisticDingdingUnitForDay_;
import com.x.attendance.entity.StatisticDingdingUnitForMonth;
import com.x.attendance.entity.StatisticDingdingUnitForMonth_;
import com.x.attendance.entity.StatisticQywxPersonForMonth;
import com.x.attendance.entity.StatisticQywxPersonForMonth_;
import com.x.attendance.entity.StatisticQywxUnitForDay;
import com.x.attendance.entity.StatisticQywxUnitForDay_;
import com.x.attendance.entity.StatisticQywxUnitForMonth;
import com.x.attendance.entity.StatisticQywxUnitForMonth_;
import com.x.base.core.project.tools.DateTools;

public class DingdingAttendanceFactory extends AbstractFactory {

    public DingdingAttendanceFactory(Business business) throws Exception {
        super(business);
    }


    /**
     * 查询所有同步记录
     *
     * @param type DingdingQywxSyncRecord.syncType_dingding DingdingQywxSyncRecord.syncType_qywx
     * @return
     * @throws Exception
     */
    public List<DingdingQywxSyncRecord> findAllSyncRecordWithType(String type) throws Exception {
        EntityManager em = this.entityManagerContainer().get(DingdingQywxSyncRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DingdingQywxSyncRecord> query = cb.createQuery(DingdingQywxSyncRecord.class);
        Root<DingdingQywxSyncRecord> root = query.from(DingdingQywxSyncRecord.class);
        Predicate p = cb.equal(root.get(DingdingQywxSyncRecord_.type), type);
        query.select(root).where(p).orderBy(cb.desc(root.get(DingdingQywxSyncRecord_.startTime)));
        return em.createQuery(query).getResultList();
    }

    /**
     * 查询冲突的钉钉同步记录
     *
     * @param fromTime
     * @param toTime
     * @return
     * @throws Exception
     */
    public List<DingdingQywxSyncRecord> findConflictSyncRecord(long fromTime, long toTime) throws Exception {
        EntityManager em = this.entityManagerContainer().get(DingdingQywxSyncRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DingdingQywxSyncRecord> query = cb.createQuery(DingdingQywxSyncRecord.class);
        Root<DingdingQywxSyncRecord> root = query.from(DingdingQywxSyncRecord.class);
        Predicate p = cb.equal(root.get(DingdingQywxSyncRecord_.type), DingdingQywxSyncRecord.syncType_dingding);
        Predicate p1 = cb.or(cb.between(root.get(DingdingQywxSyncRecord_.dateFrom), fromTime, toTime), cb.between(root.get(DingdingQywxSyncRecord_.dateTo), fromTime, toTime));
        p = cb.and(p, p1);
        query.select(root).where(p);
        return em.createQuery(query).getResultList();
    }

    /**
     * 根据用户查询一段时间内的打开数据
     *
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     * @throws Exception
     */
    public List<AttendanceDingtalkDetail> findAllDingdingAttendanceDetail(Date startTime, Date endTime, String userId) throws Exception {
        if (startTime == null && endTime == null && userId == null) {
            throw new ExceptionDingdingFindNoArgumentError();
        }
        EntityManager em = this.entityManagerContainer().get(AttendanceDingtalkDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceDingtalkDetail> query = cb.createQuery(AttendanceDingtalkDetail.class);
        Root<AttendanceDingtalkDetail> root = query.from(AttendanceDingtalkDetail.class);
        Predicate p = null;
        if (startTime != null && endTime != null) {
            long start = startTime.getTime();
            long end = endTime.getTime();
            p = cb.between(root.get(AttendanceDingtalkDetail_.userCheckTime), start, end);
        }
        if (userId != null && !userId.isEmpty()) {
            if (p != null) {
                p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.userId), userId));
            } else {
                p = cb.equal(root.get(AttendanceDingtalkDetail_.userId), userId);
            }
        }
        query.select(root).where(p).orderBy(cb.desc(root.get(AttendanceDingtalkDetail_.userCheckTime)));
        return em.createQuery(query).getResultList();
    }

    /**
     * 企业微信 打卡数据查询
     *
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     * @throws Exception
     */
    public List<AttendanceQywxDetail> findQywxAttendanceDetail(Date startTime, Date endTime, String userId) throws Exception {
        if (startTime == null && endTime == null && userId == null) {
            throw new ExceptionQywxFindNoArgumentError();
        }
        EntityManager em = this.entityManagerContainer().get(AttendanceQywxDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceQywxDetail> query = cb.createQuery(AttendanceQywxDetail.class);
        Root<AttendanceQywxDetail> root = query.from(AttendanceQywxDetail.class);

        Predicate p = null;
        if (startTime != null && endTime != null) {
            p = cb.between(root.get(AttendanceQywxDetail_.checkin_time_date), startTime, endTime);
        }
        if (userId != null && !userId.isEmpty()) {
            if (p != null) {
                p = cb.and(p, cb.equal(root.get(AttendanceQywxDetail_.userid), userId));
            } else {
                p = cb.equal(root.get(AttendanceQywxDetail_.userid), userId);
            }
        }
        query.select(root).where(p).orderBy(cb.desc(root.get(AttendanceQywxDetail_.checkin_time_date)));
        return em.createQuery(query).getResultList();

    }


    /**
     * 人员统计数据
     * @param person
     * @param year
     * @param month
     * @return
     * @throws Exception
     */
    public List<StatisticDingdingPersonForMonth> findPersonStatistic(String person, String year, String month) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingPersonForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StatisticDingdingPersonForMonth> query = cb.createQuery(StatisticDingdingPersonForMonth.class);
        Root<StatisticDingdingPersonForMonth> root = query.from(StatisticDingdingPersonForMonth.class);
        Predicate p = cb.equal(root.get(StatisticDingdingPersonForMonth_.o2User), person);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingPersonForMonth_.statisticYear), year));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingPersonForMonth_.statisticMonth), month));

        query.select(root).where(p);
        return em.createQuery(query).getResultList();
    }

    /**
     * 人员统计数据
     * @param unit
     * @param year
     * @param month
     * @return
     * @throws Exception
     */
    public List<StatisticDingdingPersonForMonth> findPersonStatisticWithUnit(String unit, String year, String month) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingPersonForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StatisticDingdingPersonForMonth> query = cb.createQuery(StatisticDingdingPersonForMonth.class);
        Root<StatisticDingdingPersonForMonth> root = query.from(StatisticDingdingPersonForMonth.class);
        Predicate p = cb.equal(root.get(StatisticDingdingPersonForMonth_.o2Unit), unit);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingPersonForMonth_.statisticYear), year));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingPersonForMonth_.statisticMonth), month));

        query.select(root).where(p);
        return em.createQuery(query).getResultList();
    }


    /**
     * 部门统计数据
     * @param unit
     * @param year
     * @param month
     * @return
     * @throws Exception
     */
    public List<StatisticDingdingUnitForMonth> findUnitStatistic(String unit, String year, String month) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StatisticDingdingUnitForMonth> query = cb.createQuery(StatisticDingdingUnitForMonth.class);
        Root<StatisticDingdingUnitForMonth> root = query.from(StatisticDingdingUnitForMonth.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForMonth_.o2Unit), unit);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForMonth_.statisticYear), year));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForMonth_.statisticMonth), month));

        query.select(root).where(p);
        return em.createQuery(query).getResultList();
    }

    ////////////////////////////////统计/////////////////////////////

    /**
     * 钉钉考勤 个人统计
     * @param year
     * @param month
     * @param person
     * @param duty
     * @return
     * @throws Exception
     */
    public Long dingdingPersonForMonthDutyTimesCount(String year, String month, String person, String duty) throws Exception {
        Date start = monthFirstDay(year, month);
        Date end = monthLastDay(year, month);
        EntityManager em = this.entityManagerContainer().get(AttendanceDingtalkDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<AttendanceDingtalkDetail> root = query.from(AttendanceDingtalkDetail.class);
        Predicate p = cb.between(root.get(AttendanceDingtalkDetail_.userCheckTime), start.getTime(), end.getTime());
        p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.o2User), person));
        p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.checkType), duty));
        query.select(cb.count(root)).where(p);
        return em.createQuery(query).getSingleResult();
    }

    public Long dingdingPersonForMonthTimeResultCount(String year, String month, String person, String timeresult) throws Exception {
        Date start = monthFirstDay(year, month);
        Date end = monthLastDay(year, month);
        EntityManager em = this.entityManagerContainer().get(AttendanceDingtalkDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<AttendanceDingtalkDetail> root = query.from(AttendanceDingtalkDetail.class);
        Predicate p = cb.between(root.get(AttendanceDingtalkDetail_.userCheckTime), start.getTime(), end.getTime());
        p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.o2User), person));
        p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.timeResult), timeresult));
        query.select(cb.count(root)).where(p);
        return em.createQuery(query).getSingleResult();
    }



    /**
     * StatisticDingdingPersonForMonth ids
     * @param year
     * @param month
     * @param person
     * @return
     * @throws Exception
     */
    public List<String> getStatPersonForMonthIds(String year, String month, String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingPersonForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<StatisticDingdingPersonForMonth> root = query.from(StatisticDingdingPersonForMonth.class);
        Predicate p = cb.equal(root.get(StatisticDingdingPersonForMonth_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingPersonForMonth_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingPersonForMonth_.o2User), person));
        query.select(root.get(StatisticDingdingPersonForMonth_.id)).where(p);
        return em.createQuery(query).getResultList();
    }

    /**
     * 钉钉考勤 部门出勤人数 、上班签到人数、下班班签到人数
     *
     * @param date yyyy-MM-dd
     * @param unit 单位
     * @param duty OnDuty OffDuty
     * @return
     * @throws Exception
     */
    public Long dingdingUnitForDayDutyTimesCount(String date, String unit, String duty) throws Exception {
        Date startTime = DateTools.parse(date);
        startTime = startOneDate(startTime);
        Date endTime = endOneDate(startTime);
        EntityManager em = this.entityManagerContainer().get(AttendanceDingtalkDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<AttendanceDingtalkDetail> root = query.from(AttendanceDingtalkDetail.class);
        Predicate p = null;
        long start = startTime.getTime();
        long end = endTime.getTime();
        p = cb.between(root.get(AttendanceDingtalkDetail_.userCheckTime), start, end);
        p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.o2Unit), unit));
        p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.checkType), duty));
        query.select(cb.count(root)).where(p);
        return em.createQuery(query).getSingleResult();
    }

    public Long dingdingUnitForDayTimeResultCount(String date, String unit, String timeresult) throws Exception {

        Date startTime = DateTools.parse(date);
        startTime = startOneDate(startTime);
        Date endTime = endOneDate(startTime);
        EntityManager em = this.entityManagerContainer().get(AttendanceDingtalkDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<AttendanceDingtalkDetail> root = query.from(AttendanceDingtalkDetail.class);
        Predicate p = null;
        long start = startTime.getTime();
        long end = endTime.getTime();
        p = cb.between(root.get(AttendanceDingtalkDetail_.userCheckTime), start, end);
        p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.o2Unit), unit));
        p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.timeResult), timeresult));
        query.select(cb.count(root)).where(p);
        return em.createQuery(query).getSingleResult();
    }

    /**
     * 查询所有有数据的组织 去重的
     * @param date
     * @return
     * @throws Exception
     */
    public List<String> dingdingUnitDistinct(String date) throws Exception {
        Date startTime = DateTools.parse(date);
        startTime = startOneDate(startTime);
        Date endTime = endOneDate(startTime);
        EntityManager em = this.entityManagerContainer().get(AttendanceDingtalkDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<AttendanceDingtalkDetail> root = query.from(AttendanceDingtalkDetail.class);
        Predicate p = cb.between(root.get(AttendanceDingtalkDetail_.userCheckTime), startTime.getTime(), endTime.getTime());
        query.select(root.get(AttendanceDingtalkDetail_.o2Unit)).where(p);
        return em.createQuery(query).getResultList().stream().distinct().collect(Collectors.toList());
    }


    /**
     * 获取StatitsticDingdingForMonth ids
     * @param year
     * @param month
     * @param unit
     * @return
     * @throws Exception
     */
    public List<String> getStatUnitForMonthIds(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<StatisticDingdingUnitForMonth> root = query.from(StatisticDingdingUnitForMonth.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForMonth_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForMonth_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForMonth_.o2Unit), unit));
        query.select(root.get(StatisticDingdingUnitForMonth_.id)).where(p);
        return em.createQuery(query).getResultList();
    }

    public List<String> getStatUnitForDayIds(String year, String month, String day, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticDate), day));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(root.get(StatisticDingdingUnitForDay_.id)).where(p);
        return em.createQuery(query).getResultList();
    }



    public Long sumWorkDayUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticDingdingUnitForDay_.workDayCount))).where(p);
        return em.createQuery(query).getSingleResult();
    }

    public Long sumOnDutyUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticDingdingUnitForDay_.onDutyTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumOffDutyUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticDingdingUnitForDay_.offDutyTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }

    public Long sumNormalUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticDingdingUnitForDay_.resultNormal))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumLateTimesUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticDingdingUnitForDay_.lateTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumLeaveEarlyUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticDingdingUnitForDay_.leaveEarlyTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }

    public Long sumNotSignedUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticDingdingUnitForDay_.notSignedCount))).where(p);
        return em.createQuery(query).getSingleResult();
    }

    public Long sumAbsenteeismUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticDingdingUnitForDay_.absenteeismTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }

    public Long sumSeriousLateUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticDingdingUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticDingdingUnitForDay> root = query.from(StatisticDingdingUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticDingdingUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticDingdingUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticDingdingUnitForDay_.seriousLateTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }

    /*********************企业微信统计 ******************************/


    /**
     * 部门统计数据
     * @param unit
     * @param year
     * @param month
     * @return
     * @throws Exception
     */
    public List<StatisticQywxUnitForMonth> findQywxUnitStatistic(String unit, String year, String month) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StatisticQywxUnitForMonth> query = cb.createQuery(StatisticQywxUnitForMonth.class);
        Root<StatisticQywxUnitForMonth> root = query.from(StatisticQywxUnitForMonth.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForMonth_.o2Unit), unit);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForMonth_.statisticYear), year));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForMonth_.statisticMonth), month));

        query.select(root).where(p);
        return em.createQuery(query).getResultList();
    }

    /**
     * 人员统计数据
     * @param unit
     * @param year
     * @param month
     * @return
     * @throws Exception
     */
    public List<StatisticQywxPersonForMonth> findQywxPersonStatisticWithUnit(String unit, String year, String month) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxPersonForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StatisticQywxPersonForMonth> query = cb.createQuery(StatisticQywxPersonForMonth.class);
        Root<StatisticQywxPersonForMonth> root = query.from(StatisticQywxPersonForMonth.class);
        Predicate p = cb.equal(root.get(StatisticQywxPersonForMonth_.o2Unit), unit);
        p = cb.and(p, cb.equal(root.get(StatisticQywxPersonForMonth_.statisticYear), year));
        p = cb.and(p, cb.equal(root.get(StatisticQywxPersonForMonth_.statisticMonth), month));

        query.select(root).where(p);
        return em.createQuery(query).getResultList();
    }



    /**
     * 人员统计数据
     * @param person
     * @param year
     * @param month
     * @return
     * @throws Exception
     */
    public List<StatisticQywxPersonForMonth> findQywxPersonStatistic(String person, String year, String month) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxPersonForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StatisticQywxPersonForMonth> query = cb.createQuery(StatisticQywxPersonForMonth.class);
        Root<StatisticQywxPersonForMonth> root = query.from(StatisticQywxPersonForMonth.class);
        Predicate p = cb.equal(root.get(StatisticQywxPersonForMonth_.o2User), person);
        p = cb.and(p, cb.equal(root.get(StatisticQywxPersonForMonth_.statisticYear), year));
        p = cb.and(p, cb.equal(root.get(StatisticQywxPersonForMonth_.statisticMonth), month));

        query.select(root).where(p);
        return em.createQuery(query).getResultList();
    }

    /**
     * 个人 月份 所有数据
     * @param year
     * @param month
     * @param person
     * @return
     * @throws Exception
     */
    public List<AttendanceQywxDetail> qywxPersonForMonthList(String year, String month, String person)  throws Exception {
        Date start = monthFirstDay(year, month);
        Date end = monthLastDay(year, month);

        EntityManager em = this.entityManagerContainer().get(AttendanceQywxDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceQywxDetail> query = cb.createQuery(AttendanceQywxDetail.class);
        Root<AttendanceQywxDetail> root = query.from(AttendanceQywxDetail.class);
        Predicate p = cb.between(root.get(AttendanceQywxDetail_.checkin_time_date), start, end);
        p = cb.and(p, cb.equal(root.get(AttendanceQywxDetail_.o2User), person));
        query.select(root).where(p);
        return em.createQuery(query).getResultList();
    }
    /**
     * StatisticDingdingPersonForMonth ids
     * @param year
     * @param month
     * @param person
     * @return
     * @throws Exception
     */
    public List<String> getQywxStatPersonForMonthIds(String year, String month, String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxPersonForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<StatisticQywxPersonForMonth> root = query.from(StatisticQywxPersonForMonth.class);
        Predicate p = cb.equal(root.get(StatisticQywxPersonForMonth_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxPersonForMonth_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxPersonForMonth_.o2User), person));
        query.select(root.get(StatisticQywxPersonForMonth_.id)).where(p);
        return em.createQuery(query).getResultList();
    }


    /**
     * 单位 日期 所有数据
     * @param year
     * @param month
     * @param day
     * @param unit
     * @return
     * @throws Exception
     */
    public List<AttendanceQywxDetail> qywxUnitForDayList(String year, String month, String day,  String unit)  throws Exception {
        Date startTime = DateTools.parse(year+"-"+month+"-"+day);
        startTime = startOneDate(startTime);
        Date endTime = endOneDate(startTime);
        EntityManager em = this.entityManagerContainer().get(AttendanceQywxDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceQywxDetail> query = cb.createQuery(AttendanceQywxDetail.class);
        Root<AttendanceQywxDetail> root = query.from(AttendanceQywxDetail.class);
        Predicate p = cb.between(root.get(AttendanceQywxDetail_.checkin_time_date), startTime, endTime);
        p = cb.and(p, cb.equal(root.get(AttendanceQywxDetail_.o2Unit), unit));
        query.select(root).where(p);
        return em.createQuery(query).getResultList();
    }

    /**
     * 查询所有有数据的组织 去重的
     * @param date
     * @return
     * @throws Exception
     */
    public List<String> qywxUnitDistinct(String date) throws Exception {
        Date startTime = DateTools.parse(date);
        startTime = startOneDate(startTime);
        Date endTime = endOneDate(startTime);
        EntityManager em = this.entityManagerContainer().get(AttendanceQywxDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<AttendanceQywxDetail> root = query.from(AttendanceQywxDetail.class);
        Predicate p = cb.between(root.get(AttendanceQywxDetail_.checkin_time_date), startTime, endTime);
        query.select(root.get(AttendanceQywxDetail_.o2Unit)).where(p);
        return em.createQuery(query).getResultList().stream().distinct().collect(Collectors.toList());
    }

    /**
     * 查询单位 日期 统计数据的id 删除用的
     * @param year
     * @param month
     * @param day
     * @param unit
     * @return
     * @throws Exception
     */
    public List<String> getQywxStatUnitForDayIds(String year, String month, String day, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticDate), day));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(root.get(StatisticQywxUnitForDay_.id)).where(p);
        return em.createQuery(query).getResultList();
    }

    /**
     * 查询单位 月份统计数据的id 删除用的
     * @param year
     * @param month
     * @param unit
     * @return
     * @throws Exception
     */
    public List<String> getQywxStatUnitForMonthIds(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForMonth.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<StatisticQywxUnitForMonth> root = query.from(StatisticQywxUnitForMonth.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForMonth_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForMonth_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForMonth_.o2Unit), unit));
        query.select(root.get(StatisticQywxUnitForMonth_.id)).where(p);
        return em.createQuery(query).getResultList();
    }

    public Long sumQywxWorkDayUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticQywxUnitForDay_.workDayCount))).where(p);
        return em.createQuery(query).getSingleResult();
    }

    public Long sumQywxOndutyUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticQywxUnitForDay_.onDutyTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumQywxOffDutyUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticQywxUnitForDay_.offDutyTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumQywxOutsideUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticQywxUnitForDay_.outsideDutyTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumQywxResultNormalUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticQywxUnitForDay_.resultNormal))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumQywxLatetimeUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticQywxUnitForDay_.lateTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumQywxLeaveEarlyUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticQywxUnitForDay_.leaveEarlyTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumQywxAbsenteeismUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticQywxUnitForDay_.absenteeismTimes))).where(p);
        return em.createQuery(query).getSingleResult();
    }
    public Long sumQywxNotSignUnitForDayWithMonth(String year, String month, String unit) throws Exception {
        EntityManager em = this.entityManagerContainer().get(StatisticQywxUnitForDay.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<StatisticQywxUnitForDay> root = query.from(StatisticQywxUnitForDay.class);
        Predicate p = cb.equal(root.get(StatisticQywxUnitForDay_.statisticYear), year);
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.statisticMonth), month));
        p = cb.and(p, cb.equal(root.get(StatisticQywxUnitForDay_.o2Unit), unit));
        query.select(cb.sum(root.get(StatisticQywxUnitForDay_.notSignedCount))).where(p);
        return em.createQuery(query).getSingleResult();
    }

    private Date monthLastDay(String year, String month) throws Exception {
        Calendar cal = Calendar.getInstance();
        int yearInt = Integer.parseInt(year);
        cal.set(Calendar.YEAR, yearInt);
        int monthInt = Integer.parseInt(month);
        cal.set(Calendar.MONTH, monthInt);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }
    private Date monthFirstDay(String year, String month) throws Exception {
        Calendar cal = Calendar.getInstance();
        int yearInt = Integer.parseInt(year);
        cal.set(Calendar.YEAR, yearInt);
        int monthInt = Integer.parseInt(month);
        cal.set(Calendar.MONTH, monthInt-1);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    private Date startOneDate(Date date) throws Exception {
        return DateTools.floorDate(date, null);
    }

    private Date endOneDate(Date date) throws Exception {
        Calendar cal = DateUtils.toCalendar(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
