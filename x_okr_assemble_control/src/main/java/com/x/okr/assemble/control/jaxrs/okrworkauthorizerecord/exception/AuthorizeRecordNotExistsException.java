package com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.exception;

import com.x.base.core.exception.PromptException;

public class AuthorizeRecordNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AuthorizeRecordNotExistsException( String id ) {
		super("指定ID的授权记录不存在。ID：" + id );
	}
}
