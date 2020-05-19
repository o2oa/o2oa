package com.x.teamwork.assemble.control.jaxrs.global;

import com.x.base.core.project.exception.PromptException;

class ProjectConfigPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectConfigPersistException( Throwable e ) {
		super("系统在保存项目配置信息时发生异常。" , e );
	}
	
	ProjectConfigPersistException( Throwable e, String message ) {
		super("系统在保存项目配置信息时发生异常。Message:" + message, e );
	}
	
	ProjectConfigPersistException( String message ) {
		super("系统在保存项目配置信息时发生异常。Message:" + message );
	}
}
