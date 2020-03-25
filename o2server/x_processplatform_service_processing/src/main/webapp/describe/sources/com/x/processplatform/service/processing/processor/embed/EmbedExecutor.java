package com.x.processplatform.service.processing.processor.embed;

import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.service.processing.ThisApplication;

public class EmbedExecutor {

	private static Logger logger = LoggerFactory.getLogger(EmbedExecutor.class);

	public String execute(AssginData assginData) throws Exception {
		Wo wo = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class, "work", assginData).getData(Wo.class);
		return wo.getId();
	}

	public static class Wo extends WoId {

	}

}
