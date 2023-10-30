package com.x.processplatform.core.express;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRecordProcessing extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionRecordProcessing(String id) {
		super("流程记录处理失败:{}.", id);
	}

}
