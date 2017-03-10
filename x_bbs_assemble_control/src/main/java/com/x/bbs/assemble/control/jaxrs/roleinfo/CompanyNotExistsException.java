package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class CompanyNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CompanyNotExistsException( String name ) {
		super("公司信息不存在！Company:" + name );
	}
}
