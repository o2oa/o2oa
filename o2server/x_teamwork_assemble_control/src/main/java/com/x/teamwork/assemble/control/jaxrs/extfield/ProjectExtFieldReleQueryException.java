package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.exception.PromptException;

class ProjectExtFieldReleQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectExtFieldReleQueryException( Throwable e ) {
		super("系统在查询项目扩展属性关联信息时发生异常。" , e );
	}
	
	ProjectExtFieldReleQueryException( Throwable e, String message ) {
		super("系统在查询项目扩展属性关联信息时发生异常。Message:" + message, e );
	}
	
	ProjectExtFieldReleQueryException(  String message ) {
		super("系统在查询项目扩展属性关联信息时发生异常。Message:" + message );
	}
}
