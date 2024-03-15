package com.x.processplatform.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.DataRecord;
import com.x.processplatform.core.entity.content.DataRecord_;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

/**
 * @author sword
 */
public class DataRecordFactory extends AbstractFactory {

    public DataRecordFactory(Business business) throws Exception {
        super(business);
    }

    public DataRecord getRecord(String job, String path) throws Exception {
        EntityManager em = this.entityManagerContainer().get(DataRecord.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DataRecord> cq = cb.createQuery(DataRecord.class);
        Root<DataRecord> root = cq.from(DataRecord.class);
        Predicate p = cb.equal(root.get(DataRecord_.job), job);
        p = cb.and(p, cb.equal(root.get(DataRecord_.path), path));
        cq.select(root).where(p);
        List<DataRecord> recordList = em.createQuery(cq).setMaxResults(1).getResultList();
        return ListTools.isEmpty(recordList) ? null : recordList.get(0);
    }

}
