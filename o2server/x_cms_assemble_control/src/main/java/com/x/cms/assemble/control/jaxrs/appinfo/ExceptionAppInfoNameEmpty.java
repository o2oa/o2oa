package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNameEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoNameEmpty() {
		super("应用栏目信息名称AppName为空，无法继续保存数据." );
	}
}
