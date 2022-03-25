package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;

class ActionListWithUnitSubNestedObject extends BaseAction {

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getUnitList());
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

		@FieldDescribe("组织")
		private List<String> unitList = new ArrayList<>();

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}

	public static class Wo extends com.x.base.core.project.organization.Person {

	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		List<Unit> os = business.unit().pick(wi.getUnitList());
		List<String> unitIds = new ArrayList<>();
		for (Unit o : os) {
			unitIds.add(o.getId());
			unitIds.addAll(business.unit().listSubNested(o.getId()));
		}
		unitIds = ListTools.trim(unitIds, true, true);
		List<Identity> list = new ArrayList<>();
		if(ListTools.isNotEmpty(unitIds)) {
			list = business.entityManagerContainer().fetchIn(Identity.class,
					ListTools.toList(Identity.id_FIELDNAME, Identity.person_FIELDNAME, Identity.orderNumber_FIELDNAME), Identity.unit_FIELDNAME, unitIds);
		}
		if(ListTools.isNotEmpty(list)) {
			Map<String,Integer> map = new HashMap<>();
			for(Identity identity : list){
				map.put(identity.getPerson(), identity.getOrderNumber());
			}
			for (Person o : business.person().pick(new ArrayList<>(map.keySet()))) {
				o.setOrderNumber(map.get(o.getId()));
				wos.add(this.convert(business, o, Wo.class));
			}
			wos = wos.stream()
					.sorted(Comparator.comparing(Wo::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
							.thenComparing(Comparator
									.comparing(Wo::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
					.collect(Collectors.toList());
		}
		return wos;
	}

}
