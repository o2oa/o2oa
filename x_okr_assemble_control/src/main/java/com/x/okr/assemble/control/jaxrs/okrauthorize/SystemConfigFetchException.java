package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.exception.PromptException;

class SystemConfigFetchException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SystemConfigFetchException( Throwable e, String code ) {
		super("系统参数查询时发生异常。Code:" + code, e );
	}
}
