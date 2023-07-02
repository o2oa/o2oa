package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyBusinessObject extends PromptException {


	private static final long serialVersionUID = 1486583753209029281L;

	ExceptionEmptyBusinessObject(String businessId) {
		super("无法查询到对应的业务对象！businessId: " + businessId);
	}


}
