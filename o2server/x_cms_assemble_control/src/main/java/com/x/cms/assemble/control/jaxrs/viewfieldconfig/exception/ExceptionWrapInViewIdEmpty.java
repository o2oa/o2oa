package com.x.cms.assemble.control.jaxrs.viewfieldconfig.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWrapInViewIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWrapInViewIdEmpty( ) {
		super( "系统未能获取到列所属视图ID." );
	}
}
