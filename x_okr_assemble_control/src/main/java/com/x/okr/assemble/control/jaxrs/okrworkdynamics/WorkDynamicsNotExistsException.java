package com.x.okr.assemble.control.jaxrs.okrworkdynamics;

import com.x.base.core.exception.PromptException;

class WorkDynamicsNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDynamicsNotExistsException( String id ) {
		super("指定ID的操作动态信息记录不存在。ID：" + id );
	}
}
