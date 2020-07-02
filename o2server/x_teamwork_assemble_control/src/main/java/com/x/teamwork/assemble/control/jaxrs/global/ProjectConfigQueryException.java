package com.x.teamwork.assemble.control.jaxrs.global;

import com.x.base.core.project.exception.PromptException;

class ProjectConfigQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectConfigQueryException( Throwable e ) {
		super("系统在查询项目配置信息时发生异常。" , e );
	}
	
	ProjectConfigQueryException( Throwable e, String message ) {
		super("系统在查询项目配置信息时发生异常。Message:" + message, e );
	}
}
