package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class TaskNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskNotExistsException( String id ) {
		super("指定ID的工作任务信息不存在。ID:" + id );
	}
}
