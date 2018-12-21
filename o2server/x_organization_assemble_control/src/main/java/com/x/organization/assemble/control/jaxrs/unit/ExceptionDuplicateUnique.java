package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionDuplicateUnique extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDuplicateUnique(String name, String unique) {
		super("组织 {} 的唯一标识:{},不能和已有的标识冲突.", name, unique);
	}
}
