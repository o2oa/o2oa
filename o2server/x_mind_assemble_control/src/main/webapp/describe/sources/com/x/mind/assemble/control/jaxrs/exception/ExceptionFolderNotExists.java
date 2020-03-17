package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFolderNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFolderNotExists( String id ) {
		super("目录信息对象不存在。ID:" + id );
	}
}
