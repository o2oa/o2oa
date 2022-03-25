package com.x.program.center.jaxrs.appstyle;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppStyleNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppStyleNotExist(String str) {
		super("移动客户端样式: {} 不存在.", str);
	}
}
