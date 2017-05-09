package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.exception.PromptException;

public class SystemConfigNotFetchException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SystemConfigNotFetchException( String code ) {
		super("系统异常，系统参数不存在:" + code );
	}
}
