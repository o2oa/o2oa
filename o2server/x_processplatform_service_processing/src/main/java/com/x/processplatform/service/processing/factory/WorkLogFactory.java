package com.x.processplatform.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.WorkLog_;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

public class WorkLogFactory extends AbstractFactory {

	public WorkLogFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithWork(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.work), id);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

//	public List<String> listWithJob(String job) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<WorkLog> root = cq.from(WorkLog.class);
//		Predicate p = cb.equal(root.get(WorkLog_.job), job);
//		cq.select(root.get(WorkLog_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

	/* 从一个节点到另一个节点可能有多条 */
//	public List<String> listWithFromActivityTokenWithConnected(String fromActivityToken) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<WorkLog> root = cq.from(WorkLog.class);
//		Predicate p = cb.equal(root.get(WorkLog_.fromActivityToken), fromActivityToken);
//		p = cb.and(p, cb.equal(root.get(WorkLog_.connected), true));
//		cq.select(root.get(WorkLog_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

//	public String getWithFromActivityTokenWithNotConnected(String fromActivityToken) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<WorkLog> root = cq.from(WorkLog.class);
//		Predicate p = cb.equal(root.get(WorkLog_.fromActivityToken), fromActivityToken);
//		p = cb.and(p, cb.equal(root.get(WorkLog_.connected), false));
//		cq.select(root.get(WorkLog_.id)).where(p);
//		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
//		if (!list.isEmpty()) {
//			return list.get(0);
//		} else {
//			return null;
//		}
//	}

//	public String getWithArrivedActivityTokenWithConnected(String arrivedActivityToken) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<WorkLog> root = cq.from(WorkLog.class);
//		Predicate p = cb.equal(root.get(WorkLog_.arrivedActivityToken), arrivedActivityToken);
//		p = cb.and(p, cb.equal(root.get(WorkLog_.connected), true));
//		cq.select(root.get(WorkLog_.id)).where(p);
//		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
//		if (!list.isEmpty()) {
//			return list.get(0);
//		} else {
//			return null;
//		}
//	}

	public List<String> listWithFromActivityToken(String fromActivityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.fromActivityToken), fromActivityToken);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

//	public List<String> listWithArrivedActivityToken(String arrivedActivityToken) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<WorkLog> root = cq.from(WorkLog.class);
//		Predicate p = cb.equal(root.get(WorkLog_.arrivedActivityToken), arrivedActivityToken);
//		cq.select(root.get(WorkLog_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

//	public List<String> listWithFromActivityTokenForwardNotConnected(String activityToken) throws Exception {
//		List<String> results = SetUniqueList.setUniqueList(new ArrayList<String>());
//		this.listWithFromActivityTokenForwardNotConnected(activityToken, results);
//		return results;
//	}

//	private void listWithFromActivityTokenForwardNotConnected(String activityToken, List<String> results)
//			throws Exception {
//		List<String> arrived = SetUniqueList.setUniqueList(new ArrayList<String>());
//		List<String> ids = this.listWithFromActivityToken(activityToken);
//		if (!ids.isEmpty()) {
//			for (WorkLog o : this.entityManagerContainer().fetch(ids, WorkLog.class,
//					ListTools.toList(WorkLog.arrivedActivityToken_FIELDNAME, WorkLog.connected_FIELDNAME))) {
//				if (!results.contains(o.getId())) {
//					if (o.getConnected()) {
//						if (StringUtils.isNotEmpty(o.getArrivedActivityToken())) {
//							arrived.add(o.getArrivedActivityToken());
//						}
//					} else {
//						results.add(o.getId());
//					}
//				}
//			}
//			if (!arrived.isEmpty()) {
//				for (String str : arrived) {
//					this.listWithFromActivityTokenForwardNotConnected(str, results);
//				}
//			}
//		}
//	}

//	public List<String> listWithFromActivityTokenForward(String activityToken) throws Exception {
//		List<String> results = SetUniqueList.setUniqueList(new ArrayList<String>());
//		this.listWithFromActivityTokenForward(activityToken, results);
//		return results;
//	}

//	private void listWithFromActivityTokenForward(String activityToken, List<String> results) throws Exception {
//		List<String> arrived = SetUniqueList.setUniqueList(new ArrayList<String>());
//		List<String> ids = this.listWithFromActivityToken(activityToken);
//		if (!ids.isEmpty()) {
//			for (WorkLog o : this.entityManagerContainer().fetch(ids, WorkLog.class,
//					ListTools.toList(WorkLog.arrivedActivityToken_FIELDNAME, WorkLog.connected_FIELDNAME))) {
//				if (!results.contains(o.getId())) {
//					results.add(o.getId());
//					if ((o.getConnected()) && (StringUtils.isNotEmpty(o.getArrivedActivityToken()))) {
//						arrived.add(o.getArrivedActivityToken());
//					}
//				}
//			}
//			if (!arrived.isEmpty()) {
//				for (String str : arrived) {
//					this.listWithFromActivityTokenForward(str, results);
//				}
//			}
//		}
//	}
}