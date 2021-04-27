package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCategoryInfoQueryById extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoQueryById( Throwable e, String id ) {
		super( "根据指定ID查询应用分类信息对象时发生异常。ID:{}" ,id, e );
	}
}
