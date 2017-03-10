package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class GroupNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GroupNotExistsException( String name ) {
		super("群组信息不存在！Group:" + name );
	}
}
