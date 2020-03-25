package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionModifyDataMerged extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionModifyDataMerged(String workCompletedId) {
		super("数据已归并,workCompleted:{}.", workCompletedId);
	}
}
