package com.x.query.assemble.surface.jaxrs.view;

import com.x.base.core.project.exception.PromptException;

class ExceptionExcelResultObject extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionExcelResultObject(String flag) {
		super("指定的Excel结果不存在:{}.", flag);
	}

}
