package com.x.teamwork.assemble.control.jaxrs.dynamic;

import com.x.base.core.project.exception.PromptException;

class DynamicPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DynamicPersistException( Throwable e ) {
		super("系统在保存工作交流信息时发生异常。" , e );
	}
	
	DynamicPersistException( Throwable e, String message ) {
		super("系统在保存工作交流信息时发生异常。Message:" + message, e );
	}
	
	DynamicPersistException( String message ) {
		super("系统在保存工作交流信息时发生异常。Message:" + message );
	}
}
