package com.x.attendance.assemble.control.factory.v2;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 * Created by fancyLou on 2023/1/31.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2ManagerFactory extends AbstractFactory {

    public AttendanceV2ManagerFactory(Business business) throws Exception {
        super(business);
    }

    /**
     * 查询考勤组列表
     * 分页查询需要
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param name           可以为空
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Group> listGroupWithNameByPage(Integer adjustPage,
            Integer adjustPageSize, String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Group> cq = cb.createQuery(AttendanceV2Group.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        if (StringUtils.isNotEmpty(name)) {
            Predicate p = cb.like(root.get(AttendanceV2Group_.groupName), "%" + name + "%");
            cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2Group_.createTime)));
        } else {
            cq.select(root).orderBy(cb.desc(root.get(AttendanceV2Group_.createTime)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 查询考勤组总数
     * 分页查询需要
     * 
     * @param name 可以为空
     * @return
     * @throws Exception
     */
    public Long groupCountWithName(String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        if (StringUtils.isNotEmpty(name)) {
            Predicate p = cb.like(root.get(AttendanceV2Group_.groupName), "%" + name + "%");
            return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        }
        return em.createQuery(cq.select(cb.count(root))).getSingleResult();
    }

    /**
     * 查询班次列表
     * 分页查询需要
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param name           可以为空
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Shift> listShiftWithNameByPage(Integer adjustPage,
            Integer adjustPageSize, String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Shift.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Shift> cq = cb.createQuery(AttendanceV2Shift.class);
        Root<AttendanceV2Shift> root = cq.from(AttendanceV2Shift.class);
        if (StringUtils.isNotEmpty(name)) {
            Predicate p = cb.like(root.get(AttendanceV2Shift_.shiftName), "%" + name + "%");
            cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2Shift_.createTime)));
        } else {
            cq.select(root).orderBy(cb.desc(root.get(AttendanceV2Shift_.createTime)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 查询班次总数
     * 分页查询需要
     * 
     * @param name 可以为空
     * @return
     * @throws Exception
     */
    public Long shiftCountWithName(String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Shift.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2Shift> root = cq.from(AttendanceV2Shift.class);
        if (StringUtils.isNotEmpty(name)) {
            Predicate p = cb.like(root.get(AttendanceV2Shift_.shiftName), "%" + name + "%");
            return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        }
        return em.createQuery(cq.select(cb.count(root))).getSingleResult();
    }

    /**
     * 根据班次id，查询使用到这个班次的所有考勤组
     * 
     * @param shiftId
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Group> listGroupWithShiftId(String shiftId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Group> cq = cb.createQuery(AttendanceV2Group.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        Predicate p = cb.equal(root.get(AttendanceV2Group_.shiftId), shiftId);
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 根据工作场所id，查询使用到这个工作场所的所有考勤组
     * 
     * @param workPlaceId
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Group> listGroupWithWorkPlaceId(String workPlaceId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Group> cq = cb.createQuery(AttendanceV2Group.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        Predicate p = cb.isMember(workPlaceId, root.get(AttendanceV2Group_.workPlaceIdList));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 查询用户所属的考勤组
     * 
     * @param person distinguishName
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Group> listGroupWithPerson(String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Group> cq = cb.createQuery(AttendanceV2Group.class);
        Root<AttendanceV2Group> root = cq.from(AttendanceV2Group.class);
        Predicate p = cb.isMember(person, root.get(AttendanceV2Group_.trueParticipantList));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 查询打卡记录
     * 
     * @param person distinguishName
     * @param date   yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public List<AttendanceV2CheckInRecord> listRecordWithPersonAndDate(String person, String date) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2CheckInRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2CheckInRecord> cq = cb.createQuery(AttendanceV2CheckInRecord.class);
        Root<AttendanceV2CheckInRecord> root = cq.from(AttendanceV2CheckInRecord.class);
        Predicate p = cb.equal(root.get(AttendanceV2CheckInRecord_.userId), person);
        p = cb.and(p, cb.equal(root.get(AttendanceV2CheckInRecord_.recordDateString), date));
        return em
                .createQuery(cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2CheckInRecord_.preDutyTime))))
                .getResultList();
    }

    /**
     * 查询打卡记录
     * 分页查询需要
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param userId
     * @param startDate      Date
     * @param endDate        Date
     * @return
     * @throws Exception
     */
    public List<AttendanceV2CheckInRecord> listRecordByPage(Integer adjustPage,
            Integer adjustPageSize, String userId, Date startDate, Date endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2CheckInRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2CheckInRecord> cq = cb.createQuery(AttendanceV2CheckInRecord.class);
        Root<AttendanceV2CheckInRecord> root = cq.from(AttendanceV2CheckInRecord.class);
        Predicate p = null;
        if (StringUtils.isNotEmpty(userId)) {
            p = cb.equal(root.get(AttendanceV2CheckInRecord_.userId), userId);
        }
        if (startDate != null && endDate != null) {
            if (p == null) {
                p = cb.between(root.get(AttendanceV2CheckInRecord_.recordDate), startDate, endDate);
            } else {
                p = cb.and(p, cb.between(root.get(AttendanceV2CheckInRecord_.recordDate), startDate, endDate));
            }
        }
        if (p == null) {
            cq.select(root).orderBy(cb.desc(root.get(AttendanceV2CheckInRecord_.recordDate)));
        } else {
            cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2CheckInRecord_.recordDate)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 查询打卡记录
     * 分页查询需要
     * 
     * @param userId    可以为空
     * @param startDate Date
     * @param endDate   Date
     * @return
     * @throws Exception
     */
    public Long recordCount(String userId, Date startDate, Date endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2CheckInRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2CheckInRecord> root = cq.from(AttendanceV2CheckInRecord.class);
        Predicate p = null;
        if (StringUtils.isNotEmpty(userId)) {
            p = cb.equal(root.get(AttendanceV2CheckInRecord_.userId), userId);
        }
        if (startDate != null && endDate != null) {
            if (p == null) {
                p = cb.between(root.get(AttendanceV2CheckInRecord_.recordDate), startDate, endDate);
            } else {
                p = cb.and(p, cb.between(root.get(AttendanceV2CheckInRecord_.recordDate), startDate, endDate));
            }
        }
        if (p == null) {
            cq.select(cb.count(root));
        } else {
            cq.select(cb.count(root)).where(p);
        }
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * 根据人员和日期 查询考勤详细
     * 
     * @param person
     * @param date
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Detail> listDetailWithPersonAndDate(String person, String date) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Detail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Detail> cq = cb.createQuery(AttendanceV2Detail.class);
        Root<AttendanceV2Detail> root = cq.from(AttendanceV2Detail.class);
        Predicate p = cb.equal(root.get(AttendanceV2Detail_.userId), person);
        p = cb.and(p, cb.equal(root.get(AttendanceV2Detail_.recordDateString), date));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 根据人员和开始结束日期 查询考勤详细
     * 
     * @param person
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Detail> listDetailWithPersonAndStartEndDate(String person, String startDate, String endDate)
            throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Detail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Detail> cq = cb.createQuery(AttendanceV2Detail.class);
        Root<AttendanceV2Detail> root = cq.from(AttendanceV2Detail.class);
        Predicate p = cb.equal(root.get(AttendanceV2Detail_.userId), person);
        p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), endDate));
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), startDate));
        cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2Detail_.recordDateString)));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 查询考勤详细列表
     * 分页查询需要
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param userId         可以为空
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Detail> listDetailByPage(Integer adjustPage,
            Integer adjustPageSize, String userId, String startDate, String endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Detail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Detail> cq = cb.createQuery(AttendanceV2Detail.class);
        Root<AttendanceV2Detail> root = cq.from(AttendanceV2Detail.class);
        if (StringUtils.isNotEmpty(userId)) {
            Predicate p = cb.equal(root.get(AttendanceV2Detail_.userId), userId);
            p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), endDate));
            p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), startDate));
            cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2Detail_.createTime)));
        } else {
            Predicate p = cb.lessThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), endDate);
            p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), startDate));
            cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2Detail_.createTime)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 查询考勤组总数
     * 分页查询需要
     * 
     * @param userId    可以为空
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public Long detailCount(String userId, String startDate, String endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Detail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2Detail> root = cq.from(AttendanceV2Detail.class);
        Predicate p = cb.lessThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), endDate);
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2Detail_.recordDateString), startDate));
        if (StringUtils.isNotEmpty(userId)) {
            p = cb.and(p, cb.equal(root.get(AttendanceV2Detail_.userId), userId));
        }
        return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
    }

    /**
     * 查询申诉记录 根据打卡记录的id
     * 
     * @param recordId 打卡记录的id
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AppealInfo> listAppealInfoWithRecordId(String recordId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AppealInfo> cq = cb.createQuery(AttendanceV2AppealInfo.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = cb.equal(root.get(AttendanceV2AppealInfo_.recordId), recordId);
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 查询申诉记录 根据日期查询出所有的申诉记录
     * 
     * @param recordDateString 日期
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AppealInfo> listAppealInfoWithRecordDateString(String recordDateString) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AppealInfo> cq = cb.createQuery(AttendanceV2AppealInfo.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = cb.equal(root.get(AttendanceV2AppealInfo_.recordDateString), recordDateString);
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 查询申诉记录
     * 分页查询需要
     * startDate 和 endDate 必须同时有值
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param users
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AppealInfo> listAppealInfoByPage(Integer adjustPage,
            Integer adjustPageSize, List<String> users, String startDate, String endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AppealInfo> cq = cb.createQuery(AttendanceV2AppealInfo.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = null;
        if (users != null && !users.isEmpty()) {
            p = root.get(AttendanceV2AppealInfo_.userId).in(users);
            // p = cb.equal(root.get(AttendanceV2AppealInfo_.userId), userId);
        }
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            if (p == null) {
                p = cb.lessThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), endDate);
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), startDate));
            } else {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), endDate));
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), startDate));
            }
        }
        if (p == null) {
            cq.select(root).orderBy(cb.desc(root.get(AttendanceV2AppealInfo_.recordDate)));
        } else {
            cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2AppealInfo_.recordDate)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 查询申诉记录总数
     * 分页查询需要
     * 
     * @param users
     * @return
     * @throws Exception
     */
    public Long appealCount(List<String> users, String startDate, String endDate) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = null;
        if (users != null && !users.isEmpty()) {
            p = root.get(AttendanceV2AppealInfo_.userId).in(users);
            // p = cb.equal(root.get(AttendanceV2AppealInfo_.userId), userId);
        }
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            if (p == null) {
                p = cb.lessThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), endDate);
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), startDate));
            } else {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), endDate));
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2AppealInfo_.recordDateString), startDate));
            }
        }
        if (p == null) {
            return em.createQuery(cq.select(cb.count(root))).getSingleResult();
        } else {
            return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        }
    }

    /**
     * 根据日期查询申诉记录
     * 计算申诉次数用的，不包含 init 和 admin 处理的
     * 
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param userId    可选
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AppealInfo> listAppealInfoByDateNotInit(Date startDate, Date endDate, String userId)
            throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AppealInfo.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AppealInfo> cq = cb.createQuery(AttendanceV2AppealInfo.class);
        Root<AttendanceV2AppealInfo> root = cq.from(AttendanceV2AppealInfo.class);
        Predicate p = cb.between(root.get(AttendanceV2AppealInfo_.recordDate), startDate, endDate);
        p = cb.and(p, cb.equal(root.get(AttendanceV2AppealInfo_.userId), userId));
        p = cb.and(p,cb.notEqual(root.get(AttendanceV2AppealInfo_.status), AttendanceV2AppealInfo.status_TYPE_INIT));
        p = cb.and(p,cb.notEqual(root.get(AttendanceV2AppealInfo_.status), AttendanceV2AppealInfo.status_TYPE_END_BY_ADMIN));
          
        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2AppealInfo_.recordDate)));
        return em.createQuery(cq).getResultList();
    }

    /**
     * 比传入的时间更小 未发送的消息数据
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public List<AttendanceV2AlertMessage> listAlertMessageBeforeDate(Date date) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2AlertMessage.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2AlertMessage> cq = cb.createQuery(AttendanceV2AlertMessage.class);
        Root<AttendanceV2AlertMessage> root = cq.from(AttendanceV2AlertMessage.class);
        Predicate p = cb.lessThan(root.get(AttendanceV2AlertMessage_.sendDateTime), date);
        p = cb.and(p, cb.equal(root.get(AttendanceV2AlertMessage_.sendStatus), false));
        cq.select(root).where(p);
        return em.createQuery(cq).getResultList();
    }

    /**
     * 个人配置
     * 
     * @param person
     * @return
     * @throws Exception
     */
    public List<AttendanceV2PersonConfig> personConfigWithPerson(String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2PersonConfig.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2PersonConfig> cq = cb.createQuery(AttendanceV2PersonConfig.class);
        Root<AttendanceV2PersonConfig> root = cq.from(AttendanceV2PersonConfig.class);
        Predicate p = cb.equal(root.get(AttendanceV2PersonConfig_.person), person);
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 查询人员打卡时间是否在请假数据中
     * 
     * @param person     人员
     * @param recordTime 打卡时间
     * @return
     * @throws Exception
     */
    public List<AttendanceV2LeaveData> listLeaveDataWithRecordTime(String person, Date recordTime) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2LeaveData.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveData> cq = cb.createQuery(AttendanceV2LeaveData.class);
        Root<AttendanceV2LeaveData> root = cq.from(AttendanceV2LeaveData.class);
        Predicate p = cb.equal(root.get(AttendanceV2LeaveData_.person), person);
        p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveData_.startTime), recordTime));
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveData_.endTime), recordTime));
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    /**
     * 查询请假数据
     * 分页查询需要
     * 
     * @param adjustPage
     * @param adjustPageSize
     * @param person
     * @return
     * @throws Exception
     */
    public List<AttendanceV2LeaveData> listLeaveDataByPage(Integer adjustPage,
            Integer adjustPageSize, String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2LeaveData.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveData> cq = cb.createQuery(AttendanceV2LeaveData.class);
        Root<AttendanceV2LeaveData> root = cq.from(AttendanceV2LeaveData.class);
        if (StringUtils.isNotEmpty(person)) {
            Predicate p = cb.equal(root.get(AttendanceV2LeaveData_.person), person);
            cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2LeaveData_.startTime)));
        } else {
            cq.select(root).orderBy(cb.desc(root.get(AttendanceV2LeaveData_.startTime)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 查询请假数据
     * 分页查询需要
     * 
     * @param person
     * @return
     * @throws Exception
     */
    public Long listLeaveDataCount(String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2LeaveData.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2LeaveData> root = cq.from(AttendanceV2LeaveData.class);
        if (StringUtils.isNotEmpty(person)) {
            Predicate p = cb.equal(root.get(AttendanceV2LeaveData_.person), person);
            return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        } else {
            return em.createQuery(cq.select(cb.count(root))).getSingleResult();
        }
    }
}
