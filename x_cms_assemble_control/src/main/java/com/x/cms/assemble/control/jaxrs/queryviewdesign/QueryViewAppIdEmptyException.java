package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.x.base.core.exception.PromptException;

class QueryViewAppIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	QueryViewAppIdEmptyException() {
		super("视图信息中应用栏目ID为空，无法保存视图信息。" );
	}
}
