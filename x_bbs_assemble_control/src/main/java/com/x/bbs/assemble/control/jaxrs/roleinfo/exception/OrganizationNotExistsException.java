package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.exception.PromptException;

public class OrganizationNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public OrganizationNotExistsException( String name ) {
		super("组织不存在.Organization:" + name );
	}
}
