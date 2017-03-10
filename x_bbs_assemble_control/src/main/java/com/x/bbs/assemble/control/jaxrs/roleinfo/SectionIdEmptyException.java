package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class SectionIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionIdEmptyException() {
		super("版块ID为空， 无法进行查询." );
	}
}
