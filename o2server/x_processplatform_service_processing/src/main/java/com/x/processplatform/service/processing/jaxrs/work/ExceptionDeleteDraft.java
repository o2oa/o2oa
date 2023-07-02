package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionDeleteDraft extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionDeleteDraft(Exception e, String id, String title, String sequence) {
		super(e, "删除长时间处于草稿状态的工作失败,: id:{}, title:{}, sequence:{}.", id, title, sequence);
	}

}
