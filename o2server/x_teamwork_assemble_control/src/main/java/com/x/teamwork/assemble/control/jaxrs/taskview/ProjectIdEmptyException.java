package com.x.teamwork.assemble.control.jaxrs.taskview;

import com.x.base.core.project.exception.PromptException;

class ProjectIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectIdEmptyException() {
		super("工作任务标签信息中项目ID不允许为空。" );
	}
}
