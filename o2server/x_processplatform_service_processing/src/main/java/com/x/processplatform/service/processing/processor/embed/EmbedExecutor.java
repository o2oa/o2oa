package com.x.processplatform.service.processing.processor.embed;

import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWo;
import com.x.processplatform.service.processing.ThisApplication;

public class EmbedExecutor {

	public ActionAssignCreateWo execute(ActionAssignCreateWi assignData) throws Exception {
		return ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class, "work", assignData)
				.getData(ActionAssignCreateWo.class);
	}

}
