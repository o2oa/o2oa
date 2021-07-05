package com.x.processplatform.service.processing.jaxrs.workcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionMerge extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionMerge(Exception e, String id) {
		super(e, "已完成工作合并失败, id:{}.",id);
	}

}
