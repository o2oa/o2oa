package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.query.assemble.designer.Business;
import com.x.query.assemble.designer.ThisApplication;

import java.util.Date;
import java.util.List;

class ActionBuildAll extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBuildAll.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			if (!business.controllable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<Application> apps = ThisApplication.context().applications().get(x_query_assemble_designer.class);
			if (ListTools.isNotEmpty(apps)) {
				apps.stream().forEach(o -> {
					String url = o.getUrlJaxrsRoot() + "table/build?tt="+new Date().getTime();
					logger.print("{} do dispatch build table request to : {}", effectivePerson.getDistinguishedName(), url);
					try {
						CipherConnectionAction.get(effectivePerson.getDebugger(), url);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
			wo.setValue(true);

			result.setData(wo);

			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}