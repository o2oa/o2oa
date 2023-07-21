package com.x.program.center.jaxrs.module;

import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.program.center.ThisApplication;
import com.x.program.center.WrapModule;
import com.x.program.center.core.entity.wrap.WrapServiceModule;
import com.x.query.core.entity.wrap.WrapQuery;

public class ActionOutputStructure extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionOutputStructure.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setProcessPlatformList(ThisApplication.context().applications()
				.getQuery(x_processplatform_assemble_designer.class, Applications.joinQueryUri("output", "list"))
				.getDataAsList(WrapProcessPlatform.class));
		wo.setCmsList(ThisApplication.context().applications()
				.getQuery(x_cms_assemble_control.class, Applications.joinQueryUri("output", "list"))
				.getDataAsList(WrapCms.class));
		wo.setPortalList(ThisApplication.context().applications()
				.getQuery(x_portal_assemble_designer.class, Applications.joinQueryUri("output", "list"))
				.getDataAsList(WrapPortal.class));
		wo.setQueryList(ThisApplication.context().applications()
				.getQuery(x_query_assemble_designer.class, Applications.joinQueryUri("output", "list"))
				.getDataAsList(WrapQuery.class));
		wo.setServiceModuleList(CipherConnectionAction.get(false,
				Config.url_x_program_center_jaxrs("output", "list"))
				.getDataAsList(WrapServiceModule.class));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapModule {
	}

}