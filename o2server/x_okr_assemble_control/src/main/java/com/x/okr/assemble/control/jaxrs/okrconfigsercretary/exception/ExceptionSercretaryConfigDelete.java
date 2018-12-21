package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSercretaryConfigDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSercretaryConfigDelete( Throwable e, String id ) {
		super("id为空，无法继续进行查询操作。ID:"+id, e);
	}
}
