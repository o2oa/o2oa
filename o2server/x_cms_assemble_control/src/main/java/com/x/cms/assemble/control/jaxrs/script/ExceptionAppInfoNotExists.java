package com.x.cms.assemble.control.jaxrs.script;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoNotExists(String flag ) {
		super("指定ID的应用栏目信息对象不存在。flag:" + flag );
	}
}
