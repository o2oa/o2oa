package com.x.teamwork.assemble.control.jaxrs.projectgroup;

import com.x.base.core.project.exception.PromptException;

class ProjectGroupQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectGroupQueryException( Throwable e ) {
		super("系统在查询项目组信息时发生异常。" , e );
	}
	
	ProjectGroupQueryException( Throwable e, String message ) {
		super("系统在查询项目组信息时发生异常。Message:" + message, e );
	}
}
