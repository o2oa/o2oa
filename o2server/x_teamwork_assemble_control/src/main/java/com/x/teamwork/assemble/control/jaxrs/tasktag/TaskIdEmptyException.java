package com.x.teamwork.assemble.control.jaxrs.tasktag;

import com.x.base.core.project.exception.PromptException;

class TaskIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskIdEmptyException() {
		super("工作任务标签信息中任务ID不允许为空。" );
	}
}
