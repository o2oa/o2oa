package com.x.teamwork.assemble.control.jaxrs.taskListTemplate;

import com.x.base.core.project.exception.PromptException;

class TaskListTemplateQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskListTemplateQueryException( Throwable e ) {
		super("系统在查询项目模板对应的泳道信息时发生异常。" , e );
	}
	
	TaskListTemplateQueryException( Throwable e, String message ) {
		super("系统在查询项目模板对应的泳道信息时发生异常。Message:" + message, e );
	}
}
