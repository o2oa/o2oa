package com.x.processplatform.assemble.bam.jaxrs.state;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;

class ActionCategoryTrigger extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCategoryTrigger.class);

	ActionResult<Wo> execute() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			new TimerCategory().execute(business);
			Wo wo = Wo.copier.copy(ThisApplication.state.getCategory());
			result.setData((wo));
			return result;
		}

	}

	public static class Wo extends ActionCategory.Wo {

		static WrapCopier<ActionCategory.Wo, Wo> copier = WrapCopierFactory.wo(ActionCategory.Wo.class, Wo.class, null,
				null);

	}

}
