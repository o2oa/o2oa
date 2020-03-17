package com.x.processplatform.service.processing.jaxrs.review;

import java.util.concurrent.Callable;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.service.processing.MessageFactory;

class ActionDelete extends BaseAction {

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Review review = emc.fetch(id, Review.class, ListTools.toList(Review.job_FIELDNAME));
			if (null == review) {
				throw new ExceptionEntityNotExist(id, Review.class);
			}
			executorSeed = review.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

					Review review = emc.find(id, Review.class);
					if (null == review) {
						throw new ExceptionEntityNotExist(id, Review.class);
					}
					emc.beginTransaction(Review.class);
					emc.remove(review, CheckRemoveType.all);
					emc.commit();
					MessageFactory.review_delete(review);
					wo.setId(review.getId());
				}
				return "";
			}
		};

		ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}

}
