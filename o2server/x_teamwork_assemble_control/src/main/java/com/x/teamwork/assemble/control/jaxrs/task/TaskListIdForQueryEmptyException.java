package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class TaskListIdForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskListIdForQueryEmptyException() {
		super("查询的工作任务信息的工作任务列表ID为空，无法继续查询数据。" );
	}
}
