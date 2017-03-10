package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo;

import com.x.base.core.exception.PromptException;

class WorkIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkIdEmptyException() {
		super("工作ID为空。" );
	}
}
