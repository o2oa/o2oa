package com.x.attendance.assemble.control.factory;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.attendance.entity.DingdingQywxSyncRecord_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
}
