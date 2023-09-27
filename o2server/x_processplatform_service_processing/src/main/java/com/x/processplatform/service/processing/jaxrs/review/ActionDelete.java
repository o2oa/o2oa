package com.x.processplatform.service.processing.jaxrs.review;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Review review = emc.fetch(id, Review.class, ListTools.toList(Review.job_FIELDNAME));
			if (null == review) {
				throw new ExceptionEntityNotExist(id, Review.class);
			}
			executorSeed = review.getJob();
		}

		CallableImpl impl = new CallableImpl(id);

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(impl).get(300, TimeUnit.SECONDS);
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		private CallableImpl(String id) {
			this.id = id;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {

			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();

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
			result.setData(wo);
			return result;
		}

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -1259909001998109357L;

	}

}
