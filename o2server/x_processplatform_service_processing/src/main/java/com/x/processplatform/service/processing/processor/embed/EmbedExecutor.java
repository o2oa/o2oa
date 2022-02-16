package com.x.processplatform.service.processing.processor.embed;

import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWo;
import com.x.processplatform.service.processing.ThisApplication;

public class EmbedExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbedExecutor.class);

	public ActionAssignCreateWo execute(ActionAssignCreateWi assignData) throws Exception {
		return ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class, "work", assignData)
				.getData(ActionAssignCreateWo.class);
	}

}
