package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.exception.PromptException;

class ProjectQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectQueryException( Throwable e ) {
		super("系统在查询项目信息时发生异常。" , e );
	}
	
	ProjectQueryException( Throwable e, String message ) {
		super("系统在查询项目信息时发生异常。Message:" + message, e );
	}
}
