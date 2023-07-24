package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonHasNoIdentity extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionPersonHasNoIdentity( String name ) {
		super("系统未检测到用户身份信息，请检查用户的组织分配设置。Name:{}", name );
	}
}
