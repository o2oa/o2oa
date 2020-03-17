package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppAliasEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppAliasEmpty() {
		super("应用栏目信息“栏目标识”不能为空。" );
	}
}
