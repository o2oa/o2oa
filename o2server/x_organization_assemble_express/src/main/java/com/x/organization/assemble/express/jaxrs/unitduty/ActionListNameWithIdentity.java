package com.x.organization.assemble.express.jaxrs.unitduty;

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
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

import net.sf.ehcache.Element;

class ActionListNameWithIdentity extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					StringUtils.join(wi.getIdentityList(), ","));
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

		@FieldDescribe("身份")
		private List<String> identityList = new ArrayList<>();

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

	public static class Wo extends GsonPropertyObject {

		private List<String> nameList = new ArrayList<>();

		public List<String> getNameList() {
			return nameList;
		}

		public void setNameList(List<String> nameList) {
			this.nameList = nameList;
		}

	}

	private Wo list(Business business, Wi wi) throws Exception {
		Wo wo = new Wo();
		List<Identity> os = business.identity().pick(wi.getIdentityList());
		List<String> ids = ListTools.extractProperty(os, JpaObject.id_FIELDNAME, String.class, true, true);
		if (ListTools.isNotEmpty(ids)) {
			EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<UnitDuty> root = cq.from(UnitDuty.class);
			Predicate p = root.get(UnitDuty_.identityList).in(ids);
			List<String> names = em.createQuery(cq.select(root.get(UnitDuty_.name)).where(p).distinct(true))
					.getResultList();
			if (!names.isEmpty()) {
				wo.getNameList().addAll(names);
			}
		}
		return wo;
	}

}