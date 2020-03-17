package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.exception.PromptException;

class ProjectTitleEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectTitleEmptyException() {
		super("项目信息标题不允许为空。" );
	}
}
