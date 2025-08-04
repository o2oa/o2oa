package com.x.processplatform.service.processing.jaxrs.workcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionShiftTime extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionShiftTime(Exception e, String id) {
		super(e, "调整创建时间失败, id:{}.", id);
	}

}
