package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoQueryById extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoQueryById( Throwable e, String id ) {
		super( "根据指定ID查询应用栏目信息对象时发生异常。ID:{}" ,id, e );
	}
}
