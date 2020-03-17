package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionGetTopUnitNameByIdentity extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionGetTopUnitNameByIdentity( Throwable e, String identity ) {
		super("根据用户身份查询所属顶层组织名称发生异常。Identity:" +  identity, e );
	}
}
