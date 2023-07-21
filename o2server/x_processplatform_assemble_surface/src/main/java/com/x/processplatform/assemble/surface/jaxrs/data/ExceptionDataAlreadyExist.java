package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDataAlreadyExist extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionDataAlreadyExist(String title, String workId) {
		super("指定文档已有数据 title:{} id:{}.", title, workId);
	}
}
