package com.x.cms.assemble.control.jaxrs.queryview.exception;

import com.x.base.core.exception.PromptException;

public class QueryViewNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public QueryViewNotExistsException( String flag ) {
		super("视图信息不存在，无法继续进行操作。Flag:" + flag );
	}
}
