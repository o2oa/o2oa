package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.exception.PromptException;

class SystemConfigQueryByCodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SystemConfigQueryByCodeException( Throwable e, String code ) {
		super("根据指定的Code查询系统配置时发生异常。Code:" + code, e );
	}
}
