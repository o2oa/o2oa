package com.x.teamwork.assemble.control.jaxrs.global;

import com.x.base.core.project.exception.PromptException;

class PriorityPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PriorityPersistException( Throwable e ) {
		super("系统在保存项目组信息时发生异常。" , e );
	}
	
	PriorityPersistException( Throwable e, String message ) {
		super("系统在保存项目组信息时发生异常。Message:" + message, e );
	}
	
	PriorityPersistException( String message ) {
		super("系统在保存项目组信息时发生异常。Message:" + message );
	}
}
