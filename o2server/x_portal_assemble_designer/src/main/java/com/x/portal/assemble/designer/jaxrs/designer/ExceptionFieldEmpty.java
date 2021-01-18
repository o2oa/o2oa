package com.x.portal.assemble.designer.jaxrs.designer;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFieldEmpty extends PromptException {

	public ExceptionFieldEmpty(String field) {
		super("参数: {} 值无效.", field);
	}

}
