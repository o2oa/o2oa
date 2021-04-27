package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoTaskCompletedToRetract extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionNoTaskCompletedToRetract(String workId, String workLogId) {
		super("无法撤回工作 {} , 无法找到工作日志 {} 匹配的处理人.", workId, workLogId);
	}

}
