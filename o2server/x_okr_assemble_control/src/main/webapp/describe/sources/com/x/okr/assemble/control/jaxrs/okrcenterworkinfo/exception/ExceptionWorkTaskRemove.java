package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkTaskRemove extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkTaskRemove( Throwable e ) {
		super("工作部署待办删除过程中发生异常。", e );
	}
}
