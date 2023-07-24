package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionGroupNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionGroupNotExists( String name ) {
		super("群组信息不存在！Group:" + name );
	}
}
