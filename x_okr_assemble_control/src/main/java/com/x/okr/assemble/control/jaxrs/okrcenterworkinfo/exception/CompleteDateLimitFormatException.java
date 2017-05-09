package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.exception.PromptException;

public class CompleteDateLimitFormatException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CompleteDateLimitFormatException( Throwable e, String date ) {
		super("默认完成时限日期格式不正确，要求格式为：yyyy-mm-dd! Date:'" + date +"'.", e );
	}
}
