package com.x.portal.assemble.designer.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.http.EffectivePerson;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.ListTools;
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
				&& (!this.business().organization().role().hasAny(effectivePerson.getName(),
						RoleDefinition.PortalManager))
				&& (effectivePerson.isNotUser(o.getControllerList()))
				&& effectivePerson.isNotUser(o.getCreatorPerson())) {
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
			p = cb.isMember(effectivePerson.getName(), root.get(TemplatePage_.controllerList));
			p = cb.or(p, cb.equal(root.get(TemplatePage_.creatorPerson), effectivePerson.getName()));
		}
		cq.select(root.get(TemplatePage_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq.select(root.get(TemplatePage_.id)).where(p)).getResultList();
		return list;
	}

	public List<String> listEditableWithCategory(EffectivePerson effectivePerson, String category) throws Exception {
		EntityManager em = this.business().entityManagerContainer().get(TemplatePage.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TemplatePage> root = cq.from(TemplatePage.class);
		Predicate p = cb.equal(root.get(TemplatePage_.category), category);
		if (effectivePerson.isNotManager()) {
			p = cb.and(p, cb.or(cb.isMember(effectivePerson.getName(), root.get(TemplatePage_.controllerList)),
					cb.equal(root.get(TemplatePage_.creatorPerson), effectivePerson.getName())));
		}
		cq.select(root.get(TemplatePage_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq.select(root.get(TemplatePage_.id)).where(p)).getResultList();
		return list;
	}

	public boolean editable(EffectivePerson effectivePerson, TemplatePage o) throws Exception {
		if (ListTools.isEmpty(o.getAvailableCompanyList(), o.getAvailableDepartmentList(),
				o.getAvailableIdentityList())) {
			return true;
		}
		if (effectivePerson.isManager() || (this.business().organization().role().hasAny(effectivePerson.getName(),
				RoleDefinition.PortalManager))) {
			return true;
		}
		if (effectivePerson.isUser(o.getControllerList())) {
			return true;
		}
		if (effectivePerson.isUser(o.getCreatorPerson())) {
			return true;
		}
		// List<String> identities = this.business().organization().identity()
		// .ListNameWithPerson(effectivePerson.getName());
		// if (ListTools.containsAny(identities, o.getAvailableIdentityList()))
		// {
		// return true;
		// }
		// List<String> departments =
		// this.business().organization().department()
		// .ListNameWithPerson(effectivePerson.getName());
		// if (ListTools.containsAny(departments,
		// o.getAvailableDepartmentList())) {
		// return true;
		// }
		// List<String> companies =
		// this.business().organization().company().ListNameWithPerson(effectivePerson.getName());
		// if (ListTools.containsAny(companies, o.getAvailableCompanyList())) {
		// return true;
		// }
		return false;
	}

}