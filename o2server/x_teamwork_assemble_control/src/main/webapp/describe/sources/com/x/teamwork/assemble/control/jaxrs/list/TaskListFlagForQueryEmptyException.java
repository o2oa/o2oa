package com.x.teamwork.assemble.control.jaxrs.list;

import com.x.base.core.project.exception.PromptException;

class TaskListFlagForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskListFlagForQueryEmptyException() {
		super("查询的工作任务列表信息ID为空，无法继续查询数据。" );
	}
}
