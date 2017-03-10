package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import com.x.base.core.exception.PromptException;

class GetDepartmentNameByIdentityException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetDepartmentNameByIdentityException( Throwable e, String identity ) {
		super("根据用户身份查询所属组织名称发生异常。Identity:" + identity, e );
	}
}
