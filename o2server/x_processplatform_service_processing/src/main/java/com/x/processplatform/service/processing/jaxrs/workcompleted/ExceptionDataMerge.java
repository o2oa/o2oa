package com.x.processplatform.service.processing.jaxrs.workcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionDataMerge extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionDataMerge(Exception e, String id, String title, String sequence) {
		super(e, "已成工作数据合并失败, id:{}, title:{}, sequence:{}.");
	}

}
