package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;

public class ApplicationFactory extends AbstractFactory {

	public ApplicationFactory(Business business) throws Exception {
		super(business);
	}

	/* 如果isManager列示所有应用，如果不是则判断权限 */
	public List<String> listWithPerson(EffectivePerson effectivePerson) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		cq.select(root.get(Application_.id));
		if (!effectivePerson.isManager()) {
			Predicate p = cb.isMember(effectivePerson.getName(), root.get(Application_.controllerList));
			p = cb.or(p, cb.equal(root.get(Application_.creatorPerson), effectivePerson.getName()));
			cq.where(p);
		}
		return em.createQuery(cq).getResultList();
	}

	/* 如果是isManager列示分类的有所应用，如果不是则判断权限 */
	public List<String> listWithPersonWithApplicationCategory(EffectivePerson effectivePerson,
			String applicationCategory) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = cb.equal(root.get(Application_.applicationCategory), applicationCategory);
		if (!effectivePerson.isManager()) {
			p = cb.and(p, cb.or(cb.isMember(effectivePerson.getName(), root.get(Application_.controllerList)),
					cb.equal(root.get(Application_.creatorPerson), effectivePerson.getName())));
		}
		cq.select(root.get(Application_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 如果是isManager列示所有应用，如果不是则判断权限 */
	public List<String> listApplicationCategoryWithPerson(EffectivePerson effectivePerson) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		cq.select(root.get(Application_.applicationCategory)).distinct(true);
		if (!effectivePerson.isManager()) {
			Predicate p = cb.isMember(effectivePerson.getName(), root.get(Application_.controllerList));
			p = cb.or(p, cb.equal(root.get(Application_.creatorPerson), effectivePerson.getName()));
			cq.where(p);
		}
		return em.createQuery(cq).getResultList();
	}

	/* 如果是isManager列示所有应用，如果不是则判断权限 */
	public Long countWithPersonWithApplicationCategory(EffectivePerson effectivePerson, String applicationCategory)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = cb.equal(root.get(Application_.applicationCategory), applicationCategory);
		if (!effectivePerson.isManager()) {
			p = cb.and(p, cb.or(cb.isMember(effectivePerson.getName(), root.get(Application_.controllerList)),
					cb.equal(root.get(Application_.creatorPerson), effectivePerson.getName())));
		}
		cq.select(root.get(Application_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return new Long(list.size());
	}
}