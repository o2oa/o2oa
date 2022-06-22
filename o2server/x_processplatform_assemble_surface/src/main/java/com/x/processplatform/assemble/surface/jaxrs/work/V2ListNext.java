package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import javax.persistence.criteria.Predicate;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;

class V2ListNext extends V2BaseListAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2ListNext.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, id:{}, count:{}.", effectivePerson::getDistinguishedName, () -> id, () -> count);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Predicate p = predicate(effectivePerson, business, wi);
			return this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, DESC, p);
		}
	}

}