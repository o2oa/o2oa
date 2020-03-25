package com.x.program.center.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionServiceNotExist extends PromptException {

	private static final long serialVersionUID = 4976763517919976266L;

	ExceptionServiceNotExist(String flag, String service) {
		super(service+"服务: {} 不存在.", flag);
	}
}
