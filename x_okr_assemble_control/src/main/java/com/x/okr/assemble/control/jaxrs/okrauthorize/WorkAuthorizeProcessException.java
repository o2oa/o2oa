package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.exception.PromptException;

class WorkAuthorizeProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkAuthorizeProcessException( Throwable e, String id ) {
		super("对工作进行授权操作过程中发生异常。Id:" + id );
	}
}
