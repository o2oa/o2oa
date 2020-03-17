package com.x.teamwork.assemble.control.jaxrs.taskview;

import com.x.base.core.project.exception.PromptException;

class TaskViewNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskViewNotExistsException( String id ) {
		super("指定ID的工作任务标签信息不存在。ID:" + id );
	}
}
