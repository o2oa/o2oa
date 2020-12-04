package com.x.query.assemble.surface.jaxrs.statement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.express.statement.Runtime;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;

abstract class BaseAction extends StandardJaxrsAction {

	protected CacheCategory cache = new CacheCategory(Statement.class);

	protected Runtime runtime(EffectivePerson effectivePerson, JsonElement jsonElement, Integer page, Integer size)
			throws Exception {
		Runtime runtime = new Runtime();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (null == jsonElement || jsonElement.isJsonNull()) {
				runtime.parameters = new HashMap<String, Object>();
			} else {
				runtime.parameters = XGsonBuilder.instance().fromJson(jsonElement,
						new TypeToken<Map<String, Object>>() {
						}.getType());
			}
			runtime.page = this.adjustPage(page);
			runtime.size = this.adjustSize2(size);
			Set<String> keys = runtime.parameters.keySet();
			if (keys.contains(Runtime.PARAMETER_PERSON)) {
				runtime.parameters.put(Runtime.PARAMETER_PERSON, effectivePerson.getDistinguishedName());
			}
			if (keys.contains(Runtime.PARAMETER_IDENTITYLIST)) {
				runtime.parameters.put(Runtime.PARAMETER_IDENTITYLIST,
						business.organization().identity().listWithPerson(effectivePerson));
			}
			if (keys.contains(Runtime.PARAMETER_UNITLIST)) {
				runtime.parameters.put(Runtime.PARAMETER_UNITLIST,
						business.organization().unit().listWithPerson(effectivePerson));
			}
			if (keys.contains(Runtime.PARAMETER_UNITALLLIST)) {
				runtime.parameters.put(Runtime.PARAMETER_UNITALLLIST,
						business.organization().unit().listWithPersonSupNested(effectivePerson));
			}
			if (keys.contains(Runtime.PARAMETER_GROUPLIST)) {
				runtime.parameters.put(Runtime.PARAMETER_GROUPLIST,
						business.organization().group().listWithPerson(effectivePerson));
			}
			if (keys.contains(Runtime.PARAMETER_ROLELIST)) {
				runtime.parameters.put(Runtime.PARAMETER_ROLELIST,
						business.organization().role().listWithPerson(effectivePerson));
			}
		}
		return runtime;
	}

	public Integer adjustSize2(Integer pageSize) {
		return (pageSize == null || pageSize < 1)
				? EntityManagerContainer.DEFAULT_PAGESIZE.intValue()
				: pageSize;
	}
}
