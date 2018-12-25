package com.x.processplatform.assemble.surface.jaxrs.queryview;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;

class ActionFlag extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			QueryView o = business.queryView().pick(flag, application);
			if (null == o) {
				throw new ExceptionQueryViewNotExist(flag);
			}
			if (!business.queryView().allowRead(effectivePerson, o, application)) {
				throw new Exception("insufficient permissions");
			}
			Wo wo = Wo.copier.copy(o);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends QueryView {

		private static final long serialVersionUID = 2886873983211744188L;

		public static WrapCopier<QueryView, Wo> copier = WrapCopierFactory.wo(QueryView.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}