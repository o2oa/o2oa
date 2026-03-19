package com.x.teamwork.assemble.control.jaxrs.taskListTemplate;

import com.x.base.core.project.exception.PromptException;

class TaskListTemplatePersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskListTemplatePersistException( Throwable e ) {
		super("系统在保存项目模板对应的泳道信息时发生异常。" , e );
	}
	
	TaskListTemplatePersistException( Throwable e, String message ) {
		super("系统在保存项目模板对应的泳道信息时发生异常。Message:" + message, e );
	}
	
	TaskListTemplatePersistException( String message ) {
		super("系统在保存项目模板对应的泳道信息时发生异常。Message:" + message );
	}
}
