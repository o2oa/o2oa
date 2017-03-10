package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

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
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutReadCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<ReadCompleted, WrapOutReadCompleted> readCompletedOutCopier = BeanCopyToolsBuilder
			.create(ReadCompleted.class, WrapOutReadCompleted.class, WrapOutReadCompleted.FieldsInvisible);

	static BeanCopyTools<Work, WrapOutWork> workOutCopier = BeanCopyToolsBuilder.create(Work.class, WrapOutWork.class,
			null, WrapOutWork.Excludes);

	static BeanCopyTools<WorkCompleted, WrapOutWorkCompleted> workCompletedOutCopier = BeanCopyToolsBuilder
			.create(WorkCompleted.class, WrapOutWorkCompleted.class, null, WrapOutWorkCompleted.Excludes);

	List<NameValueCountPair> listApplicationPair(Business business, EffectivePerson effectivePerson) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getName());
		cq.select(root.get(ReadCompleted_.application)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(business.application().pickName(str, ReadCompleted.class, effectivePerson.getName()));
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	Long countWithApplication(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.application), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	List<NameValueCountPair> listProcessPair(Business business, EffectivePerson effectivePerson,
			Application application) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.application), application.getId()));
		cq.select(root.get(ReadCompleted_.process)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(business.process().pickName(str, ReadCompleted.class, effectivePerson.getName()));
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	Long countWithProcess(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.process), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}