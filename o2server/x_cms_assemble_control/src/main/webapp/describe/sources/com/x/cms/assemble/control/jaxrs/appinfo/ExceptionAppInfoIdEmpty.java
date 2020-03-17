package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoIdEmpty() {
		super("应用栏目信息ID为空，无法继续查询数据。" );
	}
}
