package com.x.attendance.assemble.control;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonHasNoIdentity extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonHasNoIdentity() {
		super("用户未分配任何身份，请检查用户所在的组织信息。");
	}
	
	public ExceptionPersonHasNoIdentity(String name ) {
		super("用户'"+ name +"'未分配任何身份，请检查用户所在的组织信息。");
	}
}