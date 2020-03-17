package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.exception.PromptException;

class ProjectExtFieldRelePersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectExtFieldRelePersistException( Throwable e ) {
		super("系统在保存项目扩展属性关联信息时发生异常。" , e );
	}
	
	ProjectExtFieldRelePersistException( Throwable e, String message ) {
		super("系统在保存项目扩展属性关联信息时发生异常。Message:" + message, e );
	}
	
	ProjectExtFieldRelePersistException( String message ) {
		super("系统在保存项目扩展属性关联信息时发生异常。Message:" + message );
	}
}
