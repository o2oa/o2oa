package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class TaskProjectFlagForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskProjectFlagForQueryEmptyException() {
		super("项目ID为空，无法继续查询数据。" );
	}
}
