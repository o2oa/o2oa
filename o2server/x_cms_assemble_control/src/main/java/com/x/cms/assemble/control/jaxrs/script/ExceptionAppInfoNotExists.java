package com.x.cms.assemble.control.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoNotExists(String flag ) {
		super("指定的应用不存在：{}." + flag );
	}
}
