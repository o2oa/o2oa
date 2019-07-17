package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.exception.PromptException;

class ProjectExcutorEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectExcutorEmptyException() {
		super("项目负责人信息不允许为空。" );
	}
}
