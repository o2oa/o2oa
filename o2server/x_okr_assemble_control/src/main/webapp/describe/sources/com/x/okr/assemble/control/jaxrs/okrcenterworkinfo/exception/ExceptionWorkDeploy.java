package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkDeploy extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkDeploy( Throwable e ) {
		super("工作部署操作过程中发生异常。", e );
	}
}
