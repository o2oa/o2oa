package com.x.processplatform.service.processing.jaxrs.review;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.Review;

public class ActionDelete extends BaseAction {

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Review review = emc.find(id, Review.class, ExceptionWhen.not_found);
			emc.beginTransaction(Review.class);
			emc.remove(review, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(review.getId());
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
