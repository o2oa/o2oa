package com.x.organization.assemble.express.jaxrs.personattribute;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;

class ActionSetWithPersonWithName extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSetWithPersonWithName.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			Person person = business.person().pick(wi.getPerson());
			if (null == person) {
				wo.setValue(false);
				logger.warn("user {} set personAttribute {} fail, person {} not exist.",
						effectivePerson.getDistinguishedName(), StringUtils.join(wi.getAttributeList(), ","),
						wi.getPerson());
			} else if (!enable(business, effectivePerson, person)) {
				wo.setValue(false);
				logger.warn("user {} set personAttribute person: {}, value: {} fail, permission denied.",
						effectivePerson.getDistinguishedName(), wi.getPerson(),
						StringUtils.join(wi.getAttributeList(), ","));
			} else {
				emc.beginTransaction(PersonAttribute.class);
				PersonAttribute personAttribute = this.get(business, person, wi.getName());
				if (null == personAttribute) {
					personAttribute = new PersonAttribute();
					personAttribute.setAttributeList(ListTools.trim(wi.getAttributeList(), true, false));
					personAttribute.setName(wi.getName());
					personAttribute.setPerson(person.getId());
					emc.persist(personAttribute, CheckPersistType.all);
				} else {
					personAttribute.setAttributeList(ListTools.trim(wi.getAttributeList(), true, false));
					personAttribute.setName(wi.getName());
					personAttribute.setPerson(person.getId());
					emc.check(personAttribute, CheckPersistType.all);
				}
				wo.setValue(true);
				emc.commit();
				CacheManager.notify(PersonAttribute.class);
				CacheManager.notify(Person.class);
			}
			result.setData(wo);
			return result;
		}
	}

	private boolean enable(Business business, EffectivePerson effectivePerson, Person person) throws Exception {
		if (effectivePerson.isManager() || effectivePerson.isCipher()) {
			return true;
		}
		if (effectivePerson.isNotPerson(person.getDistinguishedName())) {
			return true;
		}
		List<String> people = business.expendGroupRoleToPerson(ListTools.toList(OrganizationDefinition.Manager,
				OrganizationDefinition.OrganizationManager, OrganizationDefinition.PersonManager));
		if (people.contains(effectivePerson.getDistinguishedName())) {
			return true;
		} else {
			return false;
		}
	}

	private PersonAttribute get(Business business, Person person, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), person.getId());
		p = cb.and(p, cb.equal(root.get(PersonAttribute_.name), name));
		List<PersonAttribute> os = em.createQuery(cq.select(root).where(p)).getResultList();
		if (!os.isEmpty()) {
			return os.get(0);
		}
		return null;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("属性值")
		List<String> attributeList = new ArrayList<>();

		@FieldDescribe("属性名称")
		private String name;

		@FieldDescribe("个人")
		private String person;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public List<String> getAttributeList() {
			return attributeList;
		}

		public void setAttributeList(List<String> attributeList) {
			this.attributeList = attributeList;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Wo extends WrapBoolean {

	}

}