package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.exception.PromptException;

public class BindOrganNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public BindOrganNameEmptyException() {
		super("组织名称为空， 无法进行查询." );
	}
}
