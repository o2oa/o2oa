package com.x.teamwork.assemble.control.jaxrs.dynamic;

import com.x.base.core.project.exception.PromptException;

class DynamicQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DynamicQueryException( Throwable e ) {
		super("系统在查询工作交流信息时发生异常。" , e );
	}
	
	DynamicQueryException( Throwable e, String message ) {
		super("系统在查询工作交流信息时发生异常。Message:" + message, e );
	}
	
	DynamicQueryException( String message ) {
		super("系统在查询工作交流信息时发生异常。Message:" + message );
	}
}
