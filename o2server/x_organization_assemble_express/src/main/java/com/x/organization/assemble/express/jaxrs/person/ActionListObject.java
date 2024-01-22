package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;
import org.apache.commons.lang3.BooleanUtils;

class ActionListObject extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getPersonList(), wi.getUseNameFind());
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

		@FieldDescribe("个人")
		private List<String> personList = new ArrayList<>();

		@FieldDescribe("是否需要根据名称查找，默认false")
		private Boolean useNameFind = false;

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public Boolean getUseNameFind() {
			return useNameFind;
		}

		public void setUseNameFind(Boolean useNameFind) {
			this.useNameFind = useNameFind;
		}

	}

	public static class Wo extends com.x.base.core.project.organization.Person {

	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		for (String str : wi.getPersonList()) {
			Person o = business.person().pick(str);
			if (o != null) {
				Wo wo = this.convert(business, o, Wo.class);
				wo.setMatchKey(str);
				wos.add(wo);
			}
		}
		if (wos.isEmpty() && BooleanUtils.isTrue(wi.getUseNameFind())) {
			List<Person> os = business.person().listWithName(wi.getPersonList());
			for (Person o : os) {
				Wo wo = this.convert(business, o, Wo.class);
				wo.setMatchKey(o.getName());
				wos.add(wo);
			}
		}
		return wos;
	}

}
