package com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAuthorizeRecordQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAuthorizeRecordQueryById( Throwable e, String id ) {
		super("查询指定ID的授权记录时发生异常。ID：" + id, e );
	}
}
