package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class TaskGroupWithProjectNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskGroupWithProjectNotExistsException( String id ) {
		super("指定项目的工作任务组信息不存在。ID:" + id );
	}
}
