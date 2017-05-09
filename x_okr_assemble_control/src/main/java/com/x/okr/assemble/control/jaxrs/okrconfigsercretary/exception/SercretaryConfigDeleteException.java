package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.exception.PromptException;

public class SercretaryConfigDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SercretaryConfigDeleteException( Throwable e, String id ) {
		super("id为空，无法继续进行查询操作。ID:"+id, e);
	}
}
