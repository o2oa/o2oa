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

class ActionTest9 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest9.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			Map<String, Object> map = new TreeMap<>();
			map.put("equalEmpty", this.equalEmpty(emc));
			map.put("equalNull", this.equalNull(emc));
			map.put("equalNotEmpty", this.equalNotEmpty(emc));
			result.setData(map);
			return result;
		}
	}

	private List<Task> equalEmpty(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.stringValue01), "");
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Task> equalNotEmpty(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.notEqual(root.get(Task_.stringValue01), "");
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Task> equalNull(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.stringValue01), null);
		List<Task> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

}
