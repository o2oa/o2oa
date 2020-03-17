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
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

import net.sf.ehcache.Element;

class ActionListWithLevel extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					StringUtils.join(wi.getLevelList(), ","));
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

		@FieldDescribe("组织层级")
		private List<Integer> levelList = new ArrayList<>();

		public List<Integer> getLevelList() {
			return levelList;
		}

		public void setLevelList(List<Integer> levelList) {
			this.levelList = levelList;
		}

	}

	public static class Wo extends WoUnitListAbstract {

	}

	private Wo list(Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = root.get(Unit_.level).in(wi.getLevelList());
		List<String> unitIds = em.createQuery(cq.select(root.get(Unit_.id)).where(p).distinct(true)).getResultList();
		Wo wo = new Wo();
		List<String> list = business.unit().listUnitDistinguishedNameSorted(unitIds);
		wo.getUnitList().addAll(list);
		return wo;
	}

}