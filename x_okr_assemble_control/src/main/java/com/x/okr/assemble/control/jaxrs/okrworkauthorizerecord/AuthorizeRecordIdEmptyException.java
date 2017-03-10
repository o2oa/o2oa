package com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord;

import com.x.base.core.exception.PromptException;

class AuthorizeRecordIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AuthorizeRecordIdEmptyException() {
		super("id为空，无法进行查询。" );
	}
}
