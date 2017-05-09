package com.x.processplatform.assemble.surface.jaxrs.review;

import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.element.Application;

class ManageDelete extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ApplicationNotExistedException(applicationFlag);
			}
			Review review = emc.find(id, Review.class);
			if (null == review) {
				throw new ReviewNotExistedException(id);
			}
			if (!StringUtils.equals(review.getApplication(), application.getId())) {
				throw new ReviewNotMatchApplicationException(id, applicationFlag);
			}
			// 需要对这个应用的管理权限
			business.application().allowControl(effectivePerson, application);
			ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
					"review/" + URLEncoder.encode(review.getId(), DefaultCharset.name));
			WrapOutId wrap = new WrapOutId(review.getId());
			result.setData(wrap);
			return result;
		}
	}

}