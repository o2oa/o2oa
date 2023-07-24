package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBindObjectTypeInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionBindObjectTypeInvalid( String type ) {
		super("传入的对象类别不正确，必须是人员、组织或者群组中的一种.Type:" + type );
	}
}
