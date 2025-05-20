package com.x.processplatform.assemble.surface.jaxrs.data;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import org.apache.commons.lang3.BooleanUtils;

class ActionGetWithJobPath6 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetWithJobPath6.class);

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String job, String path0, String path1,
			String path2, String path3, String path4, String path5, String path6) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<JsonElement> result = new ActionResult<>();
			Business business = new Business(emc);
			if (BooleanUtils.isNotTrue(
					new JobControlBuilder(effectivePerson, business, job).enableAllowVisit().build().getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, job);
			}
			result.setData(this.getData(business, job, path0, path1, path2, path3, path4, path5, path6));
			return result;
		}
	}

}
