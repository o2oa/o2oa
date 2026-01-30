package com.x.teamwork.assemble.control.jaxrs.dynamic;

import com.x.base.core.project.exception.PromptException;

class TaskNotExistsException extends PromptException {

	private static final long serialVersionUID = -4298497353100800385L;

	TaskNotExistsException(String id ) {
		super("指定ID的工作任务信息不存在。ID:" + id );
	}
}
