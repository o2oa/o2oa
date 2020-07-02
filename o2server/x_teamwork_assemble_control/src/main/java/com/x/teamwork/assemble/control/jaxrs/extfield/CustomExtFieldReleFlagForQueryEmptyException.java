package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.exception.PromptException;

class CustomExtFieldReleFlagForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CustomExtFieldReleFlagForQueryEmptyException() {
		super("查询的扩展属性关联信息ID为空，无法继续查询数据。" );
	}
}
