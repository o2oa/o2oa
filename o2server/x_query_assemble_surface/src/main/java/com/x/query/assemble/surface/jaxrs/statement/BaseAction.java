package com.x.query.assemble.surface.jaxrs.statement;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.express.statement.Runtime;

abstract class BaseAction extends StandardJaxrsAction {

    protected CacheCategory cache = new CacheCategory(Statement.class);

    protected Runtime concreteRuntime(EffectivePerson effectivePerson, JsonElement jsonElement,
            Organization organization,
            Integer page,
            Integer size) throws Exception {
        Runtime runtime = new Runtime();
        if (null == jsonElement || jsonElement.isJsonNull()) {
            runtime.parameters = new HashMap<>(16);
        } else {
            runtime.parameters = XGsonBuilder.instance().fromJson(jsonElement,
                    new TypeToken<LinkedHashMap<String, Object>>() {
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
                    organization.identity().listWithPerson(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_UNITLIST)) {
            runtime.parameters.put(Runtime.PARAMETER_UNITLIST,
                    organization.unit().listWithPerson(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_UNITALLLIST)) {
            runtime.parameters.put(Runtime.PARAMETER_UNITALLLIST,
                    organization.unit().listWithPersonSupNested(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_GROUPLIST)) {
            runtime.parameters.put(Runtime.PARAMETER_GROUPLIST,
                    organization.group().listWithPerson(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_ROLELIST)) {
            runtime.parameters.put(Runtime.PARAMETER_ROLELIST,
                    organization.role().listWithPerson(effectivePerson));
        }

        return runtime;
    }

}
