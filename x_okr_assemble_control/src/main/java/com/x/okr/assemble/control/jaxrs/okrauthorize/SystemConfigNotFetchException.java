package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.exception.PromptException;

class SystemConfigNotFetchException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SystemConfigNotFetchException( String code ) {
		super("系统异常，系统参数不存在:" + code );
	}
}
