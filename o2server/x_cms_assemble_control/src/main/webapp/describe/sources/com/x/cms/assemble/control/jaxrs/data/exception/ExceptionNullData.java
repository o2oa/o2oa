package com.x.cms.assemble.control.jaxrs.data.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionNullData extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	public ExceptionNullData() {
		super("更新的数据不能为null.");
	}
}
