package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionDuplicateWithFlag extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDuplicateWithFlag(String name, String unique) {
		super("组织的名称:{},唯一标识:{},不能和已有的标识冲突.", name, unique);
	}
}
