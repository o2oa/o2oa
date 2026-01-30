package com.x.teamwork.assemble.control.jaxrs.taskListTemplate;

import com.x.base.core.project.exception.PromptException;

class TaskListTemplateNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskListTemplateNotExistsException( String id ) {
		super("指定ID的项目模板对应的泳道信息不存在。ID:" + id );
	}
}
