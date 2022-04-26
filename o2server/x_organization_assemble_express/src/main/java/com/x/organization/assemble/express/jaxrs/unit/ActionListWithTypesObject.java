package com.x.organization.assemble.express.jaxrs.unit;

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
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ActionListWithTypesObject extends BaseAction {

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if(ListTools.isEmpty(wi.getTypeList())){
				result.setData(new ArrayList<>());
			}
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getTypeList());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, wi);
				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织类型")
		private List<String> typeList = new ArrayList<>();

		public List<String> getTypeList() {
			return typeList;
		}

		public void setTypeList(List<String> typeList) {
			this.typeList = typeList;
		}

	}

	public static class Wo extends com.x.base.core.project.organization.Unit {

	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = root.get(Unit_.typeList).in(wi.getTypeList());
		List<String> unitIds = em.createQuery(cq.select(root.get(Unit_.id)).where(p)).getResultList();
		List<Unit> units = business.unit().pick(unitIds);
		units = business.unit().sort(units);
		for (Unit o : units) {
			wos.add(this.convert(business, o, Wo.class));
		}
		return wos;
	}

}
