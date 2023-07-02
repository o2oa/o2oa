package com.x.organization.assemble.express.jaxrs.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

class ActionListWithPerson extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getPersonList());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.list(business, wi);
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人")
		private List<String> personList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

	public static class Wo extends WoRoleAbstract {

	}

	private Wo list(Business business, Wi wi) throws Exception {
		List<Person> os = business.person().pick(wi.getPersonList());
		List<String> groupIds = new ArrayList<>();
		List<String> personIds = new ArrayList<>();
		List<String> unitIds = new ArrayList<>();
		for (Person person : os) {
			personIds.add(person.getId());
			groupIds.addAll(business.group().listSupNestedWithPerson(person.getId()));
		}
		groupIds = ListTools.trim(groupIds, true, true);
		List<Identity> identities = this.identities(business, personIds);
		for (Identity o : identities) {
			unitIds.add(o.getUnit());
		}
		unitIds = ListTools.trim(unitIds, true, true);
		groupIds.addAll(this.groupsContainsUnit(business, unitIds));
		groupIds = ListTools.trim(groupIds, true, true);
		EntityManager em = business.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = root.get(Role_.personList).in(personIds);
		p = cb.or(p, root.get(Role_.groupList).in(groupIds));
		List<String> roleIds = em.createQuery(cq.select(root.get(Role_.id)).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
		Wo wo = new Wo();
		wo.getRoleList().addAll(business.role().listRoleDistinguishedNameSorted(roleIds));
		return wo;
	}

	private List<Identity> identities(Business business, List<String> personIds) throws Exception {
		return business.entityManagerContainer().fetchIn(Identity.class, ListTools.toList(Identity.unit_FIELDNAME),
				Identity.person_FIELDNAME, personIds);
	}

	private List<String> groupsContainsUnit(Business business, List<String> units) throws Exception {
		return business.entityManagerContainer().idsIn(Group.class, Group.unitList_FIELDNAME, units);
	}

}