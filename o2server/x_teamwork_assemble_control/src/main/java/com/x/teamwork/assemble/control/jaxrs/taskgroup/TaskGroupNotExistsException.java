package com.x.teamwork.assemble.control.jaxrs.taskgroup;

import com.x.base.core.project.exception.PromptException;

class TaskGroupNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskGroupNotExistsException( String id ) {
		super("指定ID的工作任务组信息不存在。ID:" + id );
	}
}
