package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class TaskFlagForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskFlagForQueryEmptyException() {
		super("查询的工作任务信息ID为空，无法继续查询数据。" );
	}
}
