package com.x.teamwork.assemble.control.jaxrs.taskgroup;

import com.x.base.core.project.exception.PromptException;

class TaskGroupFlagForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskGroupFlagForQueryEmptyException() {
		super("查询的工作任务组信息ID为空，无法继续查询数据。" );
	}
}
