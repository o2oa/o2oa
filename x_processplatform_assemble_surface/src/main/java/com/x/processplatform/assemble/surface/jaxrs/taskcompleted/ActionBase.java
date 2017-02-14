package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTaskCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;

abstract class ActionBase extends StandardJaxrsAction {

	protected static BeanCopyTools<TaskCompleted, WrapOutTaskCompleted> taskCompletedOutCopier = BeanCopyToolsBuilder
			.create(TaskCompleted.class, WrapOutTaskCompleted.class, WrapOutTaskCompleted.FieldsInvisible);

	protected static BeanCopyTools<Work, WrapOutWork> workOutCopier = BeanCopyToolsBuilder.create(Work.class,
			WrapOutWork.class, null, WrapOutWork.Excludes);

	protected static BeanCopyTools<WorkCompleted, WrapOutWorkCompleted> workCompletedOutCopier = BeanCopyToolsBuilder
			.create(WorkCompleted.class, WrapOutWorkCompleted.class, null, WrapOutWorkCompleted.Excludes);

	protected List<NameValueCountPair> listApplicationPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getName());
		cq.select(root.get(TaskCompleted_.application)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(business.application().pickName(str, TaskCompleted.class, effectivePerson.getName()));
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	protected Long countWithApplication(Business business, EffectivePerson effectivePerson, String id)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected List<NameValueCountPair> listProcessPair(Business business, EffectivePerson effectivePerson,
			Application application) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), application.getId()));
		cq.select(root.get(TaskCompleted_.process)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(business.process().pickName(str, TaskCompleted.class, effectivePerson.getName()));
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	protected Long countWithProcess(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}