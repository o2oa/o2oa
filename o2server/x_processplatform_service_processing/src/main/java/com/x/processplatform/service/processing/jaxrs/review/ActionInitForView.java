package com.x.processplatform.service.processing.jaxrs.review;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import java.util.List;

class ActionInitForView extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionInitForView.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson)
			throws Exception {

		LOGGER.debug("execute initReviewForView:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<WorkCompleted> list1 = emc.fetchAll(WorkCompleted.class, List.of(JpaObject.id_FIELDNAME));
			LOGGER.info("init workCompleted to review count:{}.", list1.size());
			for (WorkCompleted wo : list1) {
				WorkCompleted workCompleted = emc.find(wo.getId(), WorkCompleted.class);
				Long count = emc.countEqualAndEqual(Review.class, Review.job_FIELDNAME, workCompleted.getJob(),
						Review.person_FIELDNAME, EffectivePerson.CIPHER);
				if (count < 1) {
					emc.beginTransaction(Review.class);
					Review review = new Review(workCompleted, EffectivePerson.CIPHER);
					emc.persist(review, CheckPersistType.all);
					emc.commit();
				}
			}

			List<Work> list2 = emc.fetchAll(Work.class, List.of(JpaObject.id_FIELDNAME));
			LOGGER.info("init workCompleted to review count:{}.", list2.size());
			for (Work wo : list2) {
				Work work = emc.find(wo.getId(), Work.class);
				Long count = emc.countEqualAndEqual(Review.class, Review.job_FIELDNAME, work.getJob(),
						Review.person_FIELDNAME, EffectivePerson.CIPHER);
				if (count < 1) {
					emc.beginTransaction(Review.class);
					Review review = new Review(work, EffectivePerson.CIPHER);
					emc.persist(review, CheckPersistType.all);
					emc.commit();
				}
			}
		}

		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 8103324298350000501L;

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -1087312273225002327L;
	}

}
