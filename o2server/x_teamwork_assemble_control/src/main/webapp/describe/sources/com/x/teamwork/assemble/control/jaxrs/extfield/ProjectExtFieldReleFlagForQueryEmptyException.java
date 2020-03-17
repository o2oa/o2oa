package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.exception.PromptException;

class ProjectExtFieldReleFlagForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectExtFieldReleFlagForQueryEmptyException() {
		super("查询的项目扩展属性关联信息ID为空，无法继续查询数据。" );
	}
}
