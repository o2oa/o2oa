package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;

abstract class BaseAction extends StandardJaxrsAction {

//	static WrapCopier<ReadCompleted, WrapOutReadCompleted> readCompletedOutCopier = WrapCopierFactory
//			.wo(ReadCompleted.class, WrapOutReadCompleted.class, null, WrapOutReadCompleted.FieldsInvisible);
//
//	static WrapCopier<Work, WrapOutWork> workOutCopier = WrapCopierFactory.wo(Work.class, WrapOutWork.class, null,
//			WrapOutWork.Excludes);
//
//	static WrapCopier<WorkCompleted, WrapOutWorkCompleted> workCompletedOutCopier = WrapCopierFactory
//			.wo(WorkCompleted.class, WrapOutWorkCompleted.class, null, WrapOutWorkCompleted.Excludes);
	

	Long countWithApplication(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.application), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

//	List<NameValueCountPair> listProcessPair(Business business, EffectivePerson effectivePerson,
//			Application application) throws Exception {
//		List<NameValueCountPair> wraps = new ArrayList<>();
//		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
//		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getDistinguishedName());
//		p = cb.and(p, cb.equal(root.get(ReadCompleted_.application), application.getId()));
//		cq.select(root.get(ReadCompleted_.process)).where(p).distinct(true);
//		List<String> list = em.createQuery(cq).getResultList();
//		for (String str : list) {
//			NameValueCountPair o = new NameValueCountPair();
//			o.setValue(str);
//			o.setName(business.process().pickName(str, ReadCompleted.class, effectivePerson.getDistinguishedName()));
//			wraps.add(o);
//		}
//		SortTools.asc(wraps, "name");
//		return wraps;
//	}

	Long countWithProcess(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.process), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}