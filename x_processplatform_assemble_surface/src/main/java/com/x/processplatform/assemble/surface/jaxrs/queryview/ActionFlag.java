package com.x.processplatform.assemble.surface.jaxrs.queryview;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryView;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;

class ActionFlag extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionExecute.class);

	ActionResult<WrapOutQueryView> execute(EffectivePerson effectivePerson, String flag, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutQueryView> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			QueryView o = business.queryView().pick(flag, application, ExceptionWhen.not_found);
			if (!business.queryView().allowRead(effectivePerson, o, application)) {
				throw new Exception("insufficient permissions");
			}
			WrapOutQueryView wrap = outCopier.copy(o);
			result.setData(wrap);
			return result;
		}
	}

}