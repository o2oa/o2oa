package com.x.processplatform.assemble.designer.element.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
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
		if (effectivePerson.isNotManager() && (!this.business().organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
			Predicate p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList));
			p = cb.or(p, cb.equal(root.get(Application_.creatorPerson), effectivePerson.getDistinguishedName()));
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
		if (effectivePerson.isNotManager() && (!this.business().organization().person().hasRole(effectivePerson,
				OrganizationDefinition.ProcessPlatformManager))) {
			p = cb.and(p,
					cb.or(cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList)),
							cb.equal(root.get(Application_.creatorPerson), effectivePerson.getDistinguishedName())));
		}
		cq.select(root.get(Application_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	// 如果是isManager列示所有应用，如果不是则判断权限
	public Long countWithPersonWithApplicationCategory(EffectivePerson effectivePerson, String applicationCategory)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = cb.equal(root.get(Application_.applicationCategory), applicationCategory);
		if (!effectivePerson.isManager()) {
			p = cb.and(p,
					cb.or(cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList)),
							cb.equal(root.get(Application_.creatorPerson), effectivePerson.getDistinguishedName())));
		}
		cq.select(root.get(Application_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		return new Long(list.size());
	}

	public <T extends Application> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(Application::getAlias, Comparator.nullsLast(String::compareTo))
						.thenComparing(Application::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}
}