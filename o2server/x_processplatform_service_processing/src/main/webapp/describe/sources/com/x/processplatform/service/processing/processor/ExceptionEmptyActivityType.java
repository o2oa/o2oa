package com.x.processplatform.service.processing.processor;

import com.x.base.core.project.exception.RunningException;
import com.x.processplatform.core.entity.element.ActivityType;

class ExceptionEmptyActivityType extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionEmptyActivityType(String title, String id, ActivityType activityType) {
		super("destinationActivityType is null work title{}, id:{}.", title, id, activityType);
	}

}
