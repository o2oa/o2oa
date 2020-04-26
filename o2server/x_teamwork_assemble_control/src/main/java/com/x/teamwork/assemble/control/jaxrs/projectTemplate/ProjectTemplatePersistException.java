package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

import com.x.base.core.project.exception.PromptException;

class ProjectTemplatePersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectTemplatePersistException( Throwable e ) {
		super("系统在保存项目模板信息时发生异常。" , e );
	}
	
	ProjectTemplatePersistException( Throwable e, String message ) {
		super("系统在保存项目模板信息时发生异常。Message:" + message, e );
	}
	
	ProjectTemplatePersistException( String message ) {
		super("系统在保存项目模板信息时发生异常。Message:" + message );
	}
}
