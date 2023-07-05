package com.x.processplatform.assemble.surface.jaxrs.review;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.core.entity.content.Review;

/**
 * 获取Review
 * 
 * @author zhour
 *
 */
class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Review review = emc.find(id, Review.class);
			if (null == review) {
				throw new ExceptionEntityNotExist(id, Review.class);
			}
			Control control = new JobControlBuilder(effectivePerson, business, review.getJob()).enableAllowVisit()
					.build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, review);
			}
			Wo wo = Wo.copier.copy(review);
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
