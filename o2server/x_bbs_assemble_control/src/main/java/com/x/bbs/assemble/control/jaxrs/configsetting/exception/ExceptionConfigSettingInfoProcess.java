package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionConfigSettingInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigSettingInfoProcess( Throwable e, String message ) {
		super( message, e );
	}
}
