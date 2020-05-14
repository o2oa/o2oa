package com.x.teamwork.assemble.control.jaxrs.global;

import com.x.base.core.project.exception.PromptException;

class PriorityNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PriorityNotExistsException( String id ) {
		super("指定ID的优先级信息不存在。ID:" + id );
	}
}
