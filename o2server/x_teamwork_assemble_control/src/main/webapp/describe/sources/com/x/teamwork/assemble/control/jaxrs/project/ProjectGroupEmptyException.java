package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.exception.PromptException;

class ProjectGroupEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectGroupEmptyException() {
		super("项目组信息不允许为空。" );
	}
}
