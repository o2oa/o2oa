package com.x.processplatform.assemble.surface.factory.content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.WorkLog_;

public class WorkLogFactory extends AbstractFactory {

	public WorkLogFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<String> listWithArrivedActivityToken(List<String> tokenList) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = root.get(WorkLog_.arrivedActivityToken).in(tokenList);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public String getWithArrivedActivityToken(String activityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.arrivedActivityToken), activityToken);
		cq.select(root.get(WorkLog_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new Exception("find multiple workLog point to arrivedActivityToken{value:" + activityToken
					+ "}, is not impossible.");
		}
	}

	public WorkLog getWithArrivedActivityTokenObject(String activityToken) throws Exception {
		String id = this.getWithArrivedActivityToken(activityToken);
		if (StringUtils.isNotEmpty(id)) {
			return this.business().entityManagerContainer().find(id, WorkLog.class, ExceptionWhen.not_found);
		}
		return null;
	}

	public List<String> listWithFromActivityToken(List<String> tokenList) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = root.get(WorkLog_.fromActivityToken).in(tokenList);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithFromActivityToken(String activityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.fromActivityToken), activityToken);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithArrivedActivityToken(String activityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.arrivedActivityToken), activityToken);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 根据job获取Work Id
	 */
	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.job), job);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<WorkLog> listWithJobObject(String job) throws Exception {
		List<String> ids = this.listWithJob(job);
		if (ListTools.isEmpty(ids)) {
			return new ArrayList<WorkLog>();
		}
		return this.business().entityManagerContainer().list(WorkLog.class, ids);
	}

	public List<String> listWithWorkCompleted(String workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.workCompleted), workCompleted);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithWork(String work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.work), work);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	// public List<String> listWithJobWithNotConnected(String job) throws
	// Exception {
	// EntityManager em = this.entityManagerContainer().get(WorkLog.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<String> cq = cb.createQuery(String.class);
	// Root<WorkLog> root = cq.from(WorkLog.class);
	// Predicate p = cb.equal(root.get(WorkLog_.job), job);
	// p = cb.and(p, cb.equal(root.get(WorkLog_.connected), false));
	// cq.select(root.get(WorkLog_.id)).where(p);
	// return em.createQuery(cq).getResultList();
	// }

	public List<String> listWithFromActivityTokenForward(String activityToken) throws Exception {
		List<String> results = SetUniqueList.setUniqueList(new ArrayList<>());
		this.listWithFromActivityTokenForward(activityToken, results);
		return results;
	}

	private void listWithFromActivityTokenForward(String activityToken, List<String> results) throws Exception {
		List<String> arrived = SetUniqueList.setUniqueList(new ArrayList<String>());
		List<String> ids = this.listWithFromActivityToken(activityToken);
		if (!ids.isEmpty()) {
			for (WorkLog o : this.entityManagerContainer().fetch(ids, WorkLog.class,
					ListTools.toList(WorkLog.ARRIVEDACTIVITYTOKEN_FIELDNAME, WorkLog.CONNECTED_FIELDNAME))) {
				if (!results.contains(o.getId())) {
					results.add(o.getId());
					if ((o.getConnected()) && (StringUtils.isNotEmpty(o.getArrivedActivityToken()))) {
						arrived.add(o.getArrivedActivityToken());
					}
				}
			}
			if (!arrived.isEmpty()) {
				for (String str : arrived) {
					this.listWithFromActivityTokenForward(str, results);
				}
			}
		}
	}

	public List<String> listWithFromActivityTokenBackward(String activityToken) throws Exception {
		List<String> results = SetUniqueList.setUniqueList(new ArrayList<String>());
		/* 需要把开始节点先加入进去 */
		results.addAll(this.listWithFromActivityToken(activityToken));
		this.listWithFromActivityTokenBackward(activityToken, results);
		return results;
	}

	private void listWithFromActivityTokenBackward(String activityToken, List<String> results) throws Exception {
		List<String> from = SetUniqueList.setUniqueList(new ArrayList<String>());
		List<String> ids = this.listWithArrivedActivityToken(activityToken);
		if (!ids.isEmpty()) {
			for (WorkLog o : this.entityManagerContainer().fetch(ids, WorkLog.class,
					ListTools.toList(WorkLog.FROMACTIVITYTOKEN_FIELDNAME, WorkLog.CONNECTED_FIELDNAME))) {
				if (!results.contains(o.getId())) {
					results.add(o.getId());
					if ((o.getConnected()) && (StringUtils.isNotEmpty(o.getFromActivityToken()))) {
						from.add(o.getFromActivityToken());
					}
				}
			}
			if (!from.isEmpty()) {
				for (String str : from) {
					this.listWithFromActivityTokenBackward(str, results);
				}
			}
		}
	}

	public List<String> listWithArrivedActivityTokenBackward(String activityToken) throws Exception {
		List<String> results = SetUniqueList.setUniqueList(new ArrayList<String>());
		this.listWithArrivedActivityTokenBackward(activityToken, results);
		return results;
	}

	private void listWithArrivedActivityTokenBackward(String activityToken, List<String> results) throws Exception {
		List<String> from = SetUniqueList.setUniqueList(new ArrayList<String>());
		List<String> ids = this.listWithArrivedActivityToken(activityToken);
		if (!ids.isEmpty()) {
			for (WorkLog o : this.entityManagerContainer().fetch(ids, WorkLog.class,
					ListTools.toList(WorkLog.FROMACTIVITYTOKEN_FIELDNAME, WorkLog.CONNECTED_FIELDNAME))) {
				if (!results.contains(o.getId())) {
					results.add(o.getId());
					if ((o.getConnected()) && (StringUtils.isNotEmpty(o.getFromActivityToken()))) {
						from.add(o.getFromActivityToken());
					}
				}
			}
			if (!from.isEmpty()) {
				for (String str : from) {
					this.listWithArrivedActivityTokenBackward(str, results);
				}
			}
		}
	}

	public List<String> listWithFromActivityTokenForwardNotConnected(String activityToken) throws Exception {
		List<String> results = SetUniqueList.setUniqueList(new ArrayList<String>());
		this.listWithFromActivityTokenForwardNotConnected(activityToken, results);
		return results;
	}

	private void listWithFromActivityTokenForwardNotConnected(String activityToken, List<String> results)
			throws Exception {
		List<String> arrived = SetUniqueList.setUniqueList(new ArrayList<String>());
		List<String> ids = this.listWithFromActivityToken(activityToken);
		if (!ids.isEmpty()) {
			for (WorkLog o : this.entityManagerContainer().fetch(ids, WorkLog.class,
					ListTools.toList(WorkLog.ARRIVEDACTIVITYTOKEN_FIELDNAME, WorkLog.CONNECTED_FIELDNAME))) {
				if (!results.contains(o.getId())) {
					if (o.getConnected()) {
						if (StringUtils.isNotEmpty(o.getArrivedActivityToken())) {
							arrived.add(o.getArrivedActivityToken());
						}
					} else {
						results.add(o.getId());
					}
				}
			}
			if (!arrived.isEmpty()) {
				for (String str : arrived) {
					this.listWithFromActivityTokenForwardNotConnected(str, results);
				}
			}
		}
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.process), id);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithProcessWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.process), id);
		p = cb.and(p, cb.equal(root.get(WorkLog_.completed), completed));
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.application), id);
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplicationWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkLog> root = cq.from(WorkLog.class);
		Predicate p = cb.equal(root.get(WorkLog_.application), id);
		p = cb.and(p, cb.equal(root.get(WorkLog_.completed), completed));
		cq.select(root.get(WorkLog_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public <T extends WorkLog> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(WorkLog::getFromTime, Comparator.nullsLast(Date::compareTo))
						.thenComparing(WorkLog::getArrivedTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return list;
	}
}