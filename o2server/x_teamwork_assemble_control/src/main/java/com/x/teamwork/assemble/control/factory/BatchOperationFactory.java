package com.x.teamwork.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.BatchOperation;
import com.x.teamwork.core.entity.BatchOperation_;

/**
 * 批处理操作信息记录，比如需要级联删除大量的工作重新计算权限等
 */
public class BatchOperationFactory extends AbstractFactory {

	public BatchOperationFactory(Business business) throws Exception {
		super(business);
	}

	public List<BatchOperation> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BatchOperation.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BatchOperation> cq = cb.createQuery( BatchOperation.class );
		Root<BatchOperation> root = cq.from( BatchOperation.class );
		Predicate p = root.get( BatchOperation_.id).in( ids );
		cq.orderBy( cb.asc( root.get( BatchOperation_.createTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<BatchOperation> list(Integer maxCount) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BatchOperation.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BatchOperation> cq = cb.createQuery( BatchOperation.class );
		Root<BatchOperation> root = cq.from( BatchOperation.class );
		cq.orderBy( cb.asc( root.get( BatchOperation_.createTime ) ) );
		return em.createQuery(cq).setMaxResults(maxCount).getResultList();
	}
	
	public List<BatchOperation> listNotRun(Integer maxCount) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BatchOperation.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BatchOperation> cq = cb.createQuery( BatchOperation.class );
		Root<BatchOperation> root = cq.from( BatchOperation.class );
		Predicate p = cb.isFalse( root.get( BatchOperation_.isRunning ) );
		cq.orderBy( cb.asc( root.get( BatchOperation_.createTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}
}