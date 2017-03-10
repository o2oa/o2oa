package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class OrganizationNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	OrganizationNotExistsException( String name ) {
		super("组织不存在.Organization:" + name );
	}
}
