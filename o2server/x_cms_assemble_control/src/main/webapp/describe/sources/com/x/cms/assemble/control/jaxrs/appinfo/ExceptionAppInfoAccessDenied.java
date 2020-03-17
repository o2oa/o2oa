package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoAccessDenied extends PromptException {
	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoAccessDenied( String message ) {
		super( message );
	}
	
	ExceptionAppInfoAccessDenied( Throwable e, String message ) {
		super( message, e );
	}

	ExceptionAppInfoAccessDenied(String personName, String appName, String appId) {
		super("person:{} access appInfo name: {} id: {}, denied.", personName, appName, appId);
	}
}
