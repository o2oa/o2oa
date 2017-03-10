package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class QueryViewNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	QueryViewNotExistsException( String id ) {
		super("指定的默认视图信息不存在。ID:" + id );
	}
}
