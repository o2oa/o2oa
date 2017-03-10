package com.x.attendance.assemble.control.exception;

import com.x.base.core.exception.PromptException;

public class PersonHasNoIdentityException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonHasNoIdentityException() {
		super("用户未分配任何身份，请检查用户所在的部门信息。");
	}
	
	public PersonHasNoIdentityException( String name ) {
		super("用户'"+ name +"'未分配任何身份，请检查用户所在的部门信息。");
	}
}