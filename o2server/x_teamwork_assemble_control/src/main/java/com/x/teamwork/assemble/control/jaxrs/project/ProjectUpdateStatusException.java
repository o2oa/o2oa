package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.exception.PromptException;

class ProjectUpdateStatusException extends PromptException {

	private static final long serialVersionUID = 3645663595950547700L;

	ProjectUpdateStatusException(String message) {
		super("修改项目状态出错：{}",message );
	}
}
