package com.x.processplatform.assemble.surface.jaxrs.review;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;

class ActionGetWithWorkOrWorkCompleted extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
					new ExceptionEntityNotExist(workOrWorkCompleted))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			String job = business.job().findWithWorkOrWorkCompleted(workOrWorkCompleted);
			List<Review> os = emc.listEqualAndEqual(Review.class, Review.person_FIELDNAME,
					effectivePerson.getDistinguishedName(), Review.job_FIELDNAME, job);
			Wo wo = new Wo();
			if (!os.isEmpty()) {
				wo = Wo.copier.copy(os.get(0));
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Review {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Review, Wo> copier = WrapCopierFactory.wo(Review.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}