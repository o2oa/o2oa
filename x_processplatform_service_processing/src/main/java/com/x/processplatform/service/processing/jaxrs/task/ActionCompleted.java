package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.http.WrapOutId;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.service.processing.Business;

public class ActionCompleted extends ActionBase {

	protected WrapOutId execute(Business business, String id) throws Exception {
		return this.processing(business, id, ProcessingType.control);
	}

}
