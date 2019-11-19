package com.x.processplatform.assemble.surface.jaxrs.review;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapCount;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;

class ActionCountWithPerson extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String credential, JsonElement jsonElement)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			String person = business.organization().person().get(credential);
			if (StringUtils.isNotEmpty(person)) {
				Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
				wo.setCount(emc.count(Review.class, p));
			} else {
				wo.setCount(0L);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends FilterWi {

	}

	public static class Wo extends WrapCount {

	}
}