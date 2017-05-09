package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.exception.PromptException;

public class CompanyNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CompanyNotExistsException( String name ) {
		super("公司信息不存在！Company:" + name );
	}
}
