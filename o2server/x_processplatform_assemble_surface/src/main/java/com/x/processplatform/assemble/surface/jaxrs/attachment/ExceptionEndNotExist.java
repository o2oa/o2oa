package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEndNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionEndNotExist(String str) {
		super("无法找到流程: {} 的结束节点.", str);
	}

}
