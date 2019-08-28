package com.x.processplatform.assemble.surface.jaxrs.test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;

class ActionTest8 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest8.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			Map<String, Object> map = new TreeMap<>();
			map.put("equalFalse", this.equalFalse(emc));
			map.put("equalTrue", this.equalTrue(emc));
			map.put("equalNotFalse", this.equalNotFalse(emc));
			map.put("equalNotTrue", this.equalNotTrue(emc));
			map.put("equalNotFalse2", this.equalNotFalse2(emc));
			map.put("equalNotTrue2", this.equalNotTrue2(emc));
			map.put("equalNull", this.equalNull(emc));
			result.setData(map);
			return result;
		}
	}

	private List<Task> equalFalse(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.booleanValue01), false);
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Task> equalTrue(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.booleanValue01), true);
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Task> equalNotFalse(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.notEqual(root.get(Task_.booleanValue01), false);
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Task> equalNotTrue(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.notEqual(root.get(Task_.booleanValue01), true);
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Task> equalNotFalse2(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.not(cb.equal(root.get(Task_.booleanValue01), false));
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Task> equalNotTrue2(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.not(cb.equal(root.get(Task_.booleanValue01), true));
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Task> equalNull(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.isNull(root.get(Task_.booleanValue01));
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

}
