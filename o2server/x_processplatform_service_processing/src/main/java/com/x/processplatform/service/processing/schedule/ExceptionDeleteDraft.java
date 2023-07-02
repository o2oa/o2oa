package com.x.processplatform.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionDeleteDraft extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionDeleteDraft(Exception e, String id, String title, String sequence) {
		super(e, "删除停滞草稿工作失败, id:{}, title:{}, sequence:{}.");
	}

}
