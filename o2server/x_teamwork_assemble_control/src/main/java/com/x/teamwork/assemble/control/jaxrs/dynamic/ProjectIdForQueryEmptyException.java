package com.x.teamwork.assemble.control.jaxrs.dynamic;

import com.x.base.core.project.exception.PromptException;

class ProjectIdForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectIdForQueryEmptyException() {
		super("根据项目查询的工作动态时，项目ID为空，无法继续查询数据。" );
	}
}
