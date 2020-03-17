package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.exception.PromptException;

class ProjectPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectPersistException( Throwable e ) {
		super("系统在保存项目信息时发生异常。" , e );
	}
	
	ProjectPersistException( Throwable e, String message ) {
		super("系统在保存项目信息时发生异常。Message:" + message, e );
	}
	
	ProjectPersistException( String message ) {
		super("系统在保存项目信息时发生异常。Message:" + message );
	}
}
