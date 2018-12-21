package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionGetUnitNameByIdentity extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionGetUnitNameByIdentity( Throwable e, String identity ) {
		super("根据用户身份查询所属组织名称发生异常。Identity:" + identity, e );
	}
}
