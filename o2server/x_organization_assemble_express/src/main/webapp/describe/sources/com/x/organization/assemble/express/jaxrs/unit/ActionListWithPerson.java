package com.x.organization.assemble.express.jaxrs.unit;

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
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;

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

	public static class Wo extends WoUnitListAbstract {

	}

	private Wo list(Business business, Wi wi) throws Exception {
		Wo wo = new Wo();
		List<Person> os = business.person().pick(wi.getPersonList());
		if (ListTools.isNotEmpty(os)) {
			List<String> ids = ListTools.extractProperty(os, JpaObject.id_FIELDNAME, String.class, true, true);
			EntityManager em = business.entityManagerContainer().get(Identity.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Identity> root = cq.from(Identity.class);
			Predicate p = root.get(Identity_.person).in(ids);
			List<String> unitIds = em.createQuery(cq.select(root.get(Identity_.unit)).where(p).distinct(true))
					.getResultList();
			unitIds = ListTools.trim(unitIds, true, true);
			if (ListTools.isNotEmpty(unitIds)) {
				List<String> list = business.unit().listUnitDistinguishedNameSorted(unitIds);
				wo.getUnitList().addAll(list);
			}
		}
		return wo;
	}

}