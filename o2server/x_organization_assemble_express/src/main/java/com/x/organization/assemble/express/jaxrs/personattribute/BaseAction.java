package com.x.organization.assemble.express.jaxrs.personattribute;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.accredit.Empower;

class BaseAction extends StandardJaxrsAction {

	CacheCategory cacheCategory = new CacheCategory(Identity.class, Unit.class, UnitAttribute.class, UnitDuty.class,
			Role.class, Person.class, PersonAttribute.class, Group.class, Empower.class);

	protected List<String> listPersonAttributeDistinguishedNameSorted(Business business,
			List<String> personAttributeIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = root.get(PersonAttribute_.id).in(personAttributeIds);
		List<PersonAttribute> list = em.createQuery(cq.select(root).where(p)).getResultList();
		list = business.personAttribute().sort(list);
		List<String> values = ListTools.extractProperty(list, "distinguishedName", String.class, true, true);
		return values;
	}

	static class WoPersonAttributeAbstract extends GsonPropertyObject {

		@FieldDescribe("个人属性")
		private List<String> personAttributeList = new ArrayList<>();

		public List<String> getPersonAttributeList() {
			return personAttributeList;
		}

		public void setPersonAttributeList(List<String> personAttributeList) {
			this.personAttributeList = personAttributeList;
		}

	}

	protected <T extends com.x.base.core.project.organization.PersonAttribute> T convert(Business business,
			PersonAttribute personAttribute, Class<T> clz) throws Exception {
		T t = clz.newInstance();
		t.setName(personAttribute.getName());
		List<String> attrubutes = new ArrayList<>();
		if (ListTools.isNotEmpty(personAttribute.getAttributeList())) {
			attrubutes.addAll(personAttribute.getAttributeList());
		}
		t.setAttributeList(attrubutes);
		Person p = business.person().pick(personAttribute.getPerson());
		if (null != p) {
			t.setPerson(p.getDistinguishedName());
		}
		return t;
	}
}