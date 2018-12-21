package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCompleteDateLimitFormat extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCompleteDateLimitFormat( Throwable e, String date ) {
		super("默认完成时限日期格式不正确，要求格式为：yyyy-mm-dd! Date:'" + date +"'.", e );
	}
}
