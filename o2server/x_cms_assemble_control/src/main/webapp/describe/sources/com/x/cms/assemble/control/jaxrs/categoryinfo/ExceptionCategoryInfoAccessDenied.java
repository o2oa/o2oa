package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryInfoAccessDenied extends PromptException {
	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoAccessDenied( String message ) {
		super( message );
	}
	
	ExceptionCategoryInfoAccessDenied( Throwable e, String message ) {
		super( message, e );
	}

	ExceptionCategoryInfoAccessDenied(String personName, String appName, String appId) {
		super("person:{} access appInfo name: {} id: {}, denied.", personName, appName, appId);
	}
}
