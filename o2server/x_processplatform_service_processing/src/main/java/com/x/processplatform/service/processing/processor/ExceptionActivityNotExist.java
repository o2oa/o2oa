package com.x.processplatform.service.processing.processor;

import com.x.base.core.project.exception.RunningException;
import com.x.processplatform.core.entity.element.ActivityType;

class ExceptionActivityNotExist extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionActivityNotExist(String title, String id, ActivityType activityType, String activityId) {
		super("destinationActivity not exist work title{}, id:{}, actvity type:{}, id:{}.", title, id, activityType,
				activityId);
	}

}
