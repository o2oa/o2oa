package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.exception.PromptException;

class CustomExtFieldReleQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CustomExtFieldReleQueryException( Throwable e ) {
		super("系统在查询扩展属性关联信息时发生异常。" , e );
	}
	
	CustomExtFieldReleQueryException( Throwable e, String message ) {
		super("系统在查询扩展属性关联信息时发生异常。Message:" + message, e );
	}
	
	CustomExtFieldReleQueryException(  String message ) {
		super("系统在查询扩展属性关联信息时发生异常。Message:" + message );
	}
}
