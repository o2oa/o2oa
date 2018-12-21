package com.x.cms.assemble.control.jaxrs.queryview.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInsufficientPermissions extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInsufficientPermissions( String flag ) {
		super("视图操作权限不足。Flag:" + flag );
	}
}
