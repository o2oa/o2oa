package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class PersonNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PersonNotExistsException( String person ) {
		super("人员信息不存在！Person:" + person );
	}
}
