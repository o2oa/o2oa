package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWrapInViewIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionWrapInViewIdEmpty( ) {
		super( "系统未能获取到列所属视图ID." );
	}
}
