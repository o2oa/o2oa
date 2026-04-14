package com.x.teamwork.assemble.control.jaxrs.list;

import com.x.base.core.project.exception.PromptException;

class TaskListNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskListNotExistsException( String id ) {
		super("指定ID的工作任务列表信息不存在。ID:" + id );
	}
}
