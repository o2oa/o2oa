package com.x.portal.assemble.designer.factory;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.designer.AbstractFactory;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.TemplatePage;
import com.x.portal.core.entity.TemplatePage_;

public class TemplatePageFactory extends AbstractFactory {

	public TemplatePageFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TemplatePage.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TemplatePage> root = cq.from(TemplatePage.class);
		Predicate p = cb.equal(root.get(TemplatePage_.name), name);
		List<String> list = em.createQuery(cq.select(root.get(TemplatePage_.id)).where(p)).setMaxResults(1)
				.getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithAlias(String alias) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TemplatePage.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TemplatePage> root = cq.from(TemplatePage.class);
		Predicate p = cb.equal(root.get(TemplatePage_.alias), alias);
		List<String> list = em.createQuery(cq.select(root.get(TemplatePage_.id)).where(p)).setMaxResults(1)
				.getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<String> list() throws Exception {
		EntityManager em = this.entityManagerContainer().get(TemplatePage.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TemplatePage> root = cq.from(TemplatePage.class);
		List<String> list = em.createQuery(cq.select(root.get(TemplatePage_.id))).getResultList();
		return list;
	}

	public boolean checkPermission(EffectivePerson effectivePerson, TemplatePage o) throws Exception {
		if (effectivePerson.isNotManager()
				&& (!this.business().organization().person().hasRole(effectivePerson,
						OrganizationDefinition.PortalManager))
				&& (effectivePerson.isNotPerson(o.getControllerList()))
				&& effectivePerson.isNotPerson(o.getCreatorPerson())) {
			return false;
		}
		return true;
	}

	public List<String> listEditable(EffectivePerson effectivePerson) throws Exception {
		EntityManager em = this.business().entityManagerContainer().get(TemplatePage.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TemplatePage> root = cq.from(TemplatePage.class);
		Predicate p = cb.conjunction();
		if (effectivePerson.isNotManager()) {
			p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(TemplatePage_.controllerList));
			p = cb.or(p, cb.equal(root.get(TemplatePage_.creatorPerson), effectivePerson.getDistinguishedName()));
		}
		cq.select(root.get(TemplatePage_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		return list;
	}

	public List<String> listEditableWithCategory(EffectivePerson effectivePerson, String category) throws Exception {
		EntityManager em = this.business().entityManagerContainer().get(TemplatePage.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TemplatePage> root = cq.from(TemplatePage.class);
		Predicate p = cb.equal(root.get(TemplatePage_.category), category);
		if (effectivePerson.isNotManager()) {
			p = cb.and(p,
					cb.or(cb.isMember(effectivePerson.getDistinguishedName(), root.get(TemplatePage_.controllerList)),
							cb.equal(root.get(TemplatePage_.creatorPerson), effectivePerson.getDistinguishedName())));
		}
		cq.select(root.get(TemplatePage_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		return list;
	}

	public boolean editable(EffectivePerson effectivePerson, TemplatePage o) throws Exception {
		if (ListTools.isEmpty(o.getAvailableUnitList(), o.getAvailableIdentityList())) {
			return true;
		}
		if (effectivePerson.isManager() || (this.business().organization().person().hasRole(effectivePerson,
				OrganizationDefinition.PortalManager))) {
			return true;
		}
		if (effectivePerson.isPerson(o.getControllerList())) {
			return true;
		}
		if (effectivePerson.isPerson(o.getCreatorPerson())) {
			return true;
		}

		List<String> identities = this.business().organization().identity().listWithPerson(effectivePerson);
		if (ListTools.containsAny(identities, o.getAvailableIdentityList())) {
			return true;
		}
		List<String> units = this.business().organization().unit().listWithPerson(effectivePerson);
		units = this.business().organization().unit().listWithUnitSupNested(units);
		if (ListTools.containsAny(units, o.getAvailableUnitList())) {
			return true;
		}
		return false;
	}

}