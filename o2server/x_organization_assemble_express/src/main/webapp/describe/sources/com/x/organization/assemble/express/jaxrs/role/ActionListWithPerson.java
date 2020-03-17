package com.x.organization.assemble.express.jaxrs.role;

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
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

import net.sf.ehcache.Element;

class ActionListWithPerson extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					StringUtils.join(wi.getPersonList(), ","));
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((Wo) element.getObjectValue());
			} else {
				Wo wo = this.list(business, wi);
				cache.put(new Element(cacheKey, wo));
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
		for (Person person : os) {
			personIds.add(person.getId());
			groupIds.addAll(business.group().listSupNestedWithPerson(person.getId()));
		}
		groupIds = ListTools.trim(groupIds, true, true);
		EntityManager em = business.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = root.get(Role_.personList).in(personIds);
		p = cb.or(p, root.get(Role_.groupList).in(groupIds));
		List<String> roleIds = em.createQuery(cq.select(root.get(Role_.id)).where(p).distinct(true)).getResultList();
		Wo wo = new Wo();
		wo.getRoleList().addAll(business.role().listRoleDistinguishedNameSorted(roleIds));
		return wo;
	}

}