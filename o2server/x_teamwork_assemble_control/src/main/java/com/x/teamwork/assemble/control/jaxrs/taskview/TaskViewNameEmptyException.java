package com.x.teamwork.assemble.control.jaxrs.taskview;

import com.x.base.core.project.exception.PromptException;

class TaskViewNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskViewNameEmptyException() {
		super("工作任务视图名称'name'不允许为空。" );
	}
}
