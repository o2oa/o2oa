package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionQueryViewNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionQueryViewNotExists( String flag ) {
		super("指定的数据视图不存在：{}" + flag );
	}
}
