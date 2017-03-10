package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkIdEmptyException() {
		super("id为空，无法进行查询。" );
	}
}
