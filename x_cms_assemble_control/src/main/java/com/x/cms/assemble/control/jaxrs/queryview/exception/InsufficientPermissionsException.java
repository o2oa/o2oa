package com.x.cms.assemble.control.jaxrs.queryview.exception;

import com.x.base.core.exception.PromptException;

public class InsufficientPermissionsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public InsufficientPermissionsException( String flag ) {
		super("视图操作权限不足。Flag:" + flag );
	}
}
