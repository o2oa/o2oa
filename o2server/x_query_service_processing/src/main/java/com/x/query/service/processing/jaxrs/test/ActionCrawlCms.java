package com.x.query.service.processing.jaxrs.test;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.schedule.CrawlCms;

class ActionCrawlCms extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCrawlCms.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(false);
			for (Application application : ThisApplication.context().applications()
					.get(ThisApplication.context().clazz())) {
				if (StringUtils.equals(Config.node(), application.getNode())) {
					String url = application.getUrlJaxrsRoot()
							+ Applications.joinQueryUri("fireschedule", "classname", CrawlCms.class.getName());
					CipherConnectionAction.get(false, url);
					wo.setValue(true);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}

}