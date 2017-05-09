package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.exception.PromptException;

public class GroupNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public GroupNotExistsException( String name ) {
		super("群组信息不存在！Group:" + name );
	}
}
