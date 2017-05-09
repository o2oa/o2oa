package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.exception.PromptException;

public class GetCompanyNameByIdentityException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public GetCompanyNameByIdentityException( Throwable e, String identity ) {
		super("根据用户身份查询所属公司名称发生异常。Identity:" +  identity, e );
	}
}
