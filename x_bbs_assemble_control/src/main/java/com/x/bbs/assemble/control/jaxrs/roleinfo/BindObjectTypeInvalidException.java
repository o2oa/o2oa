package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class BindObjectTypeInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	BindObjectTypeInvalidException( String type ) {
		super("传入的对象类别不正确，必须是人员、部门、公司或者群组中的一种.Type:" + type );
	}
}
