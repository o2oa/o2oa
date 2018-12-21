package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonIdentityInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionPersonIdentityInvalid( String identity ) {
		super("系统未检测到指定的用户身份信息，请检查用户的身份信息是否正确。Identity:" + identity );
	}
}
