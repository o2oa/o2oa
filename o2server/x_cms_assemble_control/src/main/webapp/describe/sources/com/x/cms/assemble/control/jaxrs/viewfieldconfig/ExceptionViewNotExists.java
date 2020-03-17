package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.project.exception.PromptException;

class ExceptionViewNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewNotExists( String id ) {
		super( "列表视图信息不存在。ID:" + id );
	}
}
