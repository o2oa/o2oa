package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyData extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionEmptyData() {
		super("更新的数据不能为空.");
	}
}
