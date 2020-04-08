package com.x.query.assemble.designer.jaxrs.statement;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.express.statement.Runtime;

abstract class BaseAction extends StandardJaxrsAction {

	protected Runtime runtime(EffectivePerson effectivePerson, JsonElement jsonElement, Business business, Integer page,
			Integer size) throws Exception {
		Runtime runtime = new Runtime();
		if (null == jsonElement || jsonElement.isJsonNull()) {
			runtime.parameters = new HashMap<String, Object>();
		} else {
			runtime.parameters = XGsonBuilder.instance().fromJson(jsonElement, new TypeToken<Map<String, Object>>() {
			}.getType());
		}
		runtime.page = this.adjustPage(page);
		runtime.size = this.adjustSize(size);
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

		return runtime;
	}

}
