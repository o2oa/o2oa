package com.x.processplatform.service.processing.jaxrs.review;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.WrapOutId;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.service.processing.Business;

public class ActionDelete extends BaseAction {

	protected WrapOutId execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Review review = emc.find(id, Review.class, ExceptionWhen.not_found);
		emc.beginTransaction(Review.class);
		emc.remove(review, CheckRemoveType.all);
		emc.commit();
		return new WrapOutId(review.getId());
	}

}
