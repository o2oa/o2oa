package com.x.attendance.assemble.control.factory;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.exception.DingdingFindNoArgumentError;
import com.x.attendance.entity.AttendanceDingtalkDetail;
import com.x.attendance.entity.AttendanceDingtalkDetail_;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.attendance.entity.DingdingQywxSyncRecord_;
import com.x.base.core.project.tools.StringTools;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public class DingdingAttendanceFactory extends AbstractFactory {

    public DingdingAttendanceFactory(Business business) throws Exception {
        super(business);
    }

    /**
     * 查询所有钉钉同步记录
     * @return
     * @throws Exception
     */
    public List<DingdingQywxSyncRecord> findAllDingdingSyncRecord() throws Exception {
        EntityManager em = this.entityManagerContainer().get(DingdingQywxSyncRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DingdingQywxSyncRecord> query = cb.createQuery(DingdingQywxSyncRecord.class);
        Root<DingdingQywxSyncRecord> root = query.from(DingdingQywxSyncRecord.class);
        Predicate p = cb.equal(root.get(DingdingQywxSyncRecord_.type), DingdingQywxSyncRecord.syncType_dingding);
        query.select(root).where(p).orderBy(cb.desc(root.get(DingdingQywxSyncRecord_.startTime)));
        return em.createQuery(query).getResultList();
    }

    /**
     * 根据用户查询一段时间内的打开数据
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     * @throws Exception
     */
    public List<AttendanceDingtalkDetail> findAllDingdingAttendanceDetail(Date startTime, Date endTime, String userId) throws Exception {
        if (startTime == null && endTime == null && userId == null) {
            throw new DingdingFindNoArgumentError();
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
        if (userId != null) {
            if (p != null) {
                p = cb.and(p, cb.equal(root.get(AttendanceDingtalkDetail_.userId), userId));
            }else {
                p = cb.equal(root.get(AttendanceDingtalkDetail_.userId), userId);
            }
        }
        query.select(root).where(p).orderBy(cb.desc(root.get(AttendanceDingtalkDetail_.userCheckTime)));
        return em.createQuery(query).getResultList();
    }
}
