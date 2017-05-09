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
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

public class PortalFactory extends AbstractFactory {

	public PortalFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.equal(root.get(Portal_.name), name);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithAlias(String alias) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.equal(root.get(Portal_.alias), alias);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<String> list() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id))).getResultList();
		return list;
	}

	public boolean checkPermission(EffectivePerson effectivePerson, Portal portal) throws Exception {
		if (effectivePerson.isNotManager()
				&& (!this.business().organization().role().hasAny(effectivePerson.getName(),
						RoleDefinition.PortalManager))
				&& (effectivePerson.isNotUser(portal.getControllerList()))
				&& effectivePerson.isNotUser(portal.getCreatorPerson())) {
			return false;
		}
		return true;
	}

	public boolean editable(EffectivePerson effectivePerson, Portal o) throws Exception {
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