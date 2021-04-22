package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionViewNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewNotExists( String id ) {
		super( "列表不存在，无法继续进行列表数据查询。ID:{}.", id );
	}
}
