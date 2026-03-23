package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.exception.PromptException;

class CustomExtFieldRelePersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CustomExtFieldRelePersistException( Throwable e ) {
		super("系统在保存扩展属性关联信息时发生异常。" , e );
	}
	
	CustomExtFieldRelePersistException( Throwable e, String message ) {
		super("系统在保存扩展属性关联信息时发生异常。Message:" + message, e );
	}
	
	CustomExtFieldRelePersistException( String message ) {
		super("系统在保存扩展属性关联信息时发生异常。Message:" + message );
	}
}
