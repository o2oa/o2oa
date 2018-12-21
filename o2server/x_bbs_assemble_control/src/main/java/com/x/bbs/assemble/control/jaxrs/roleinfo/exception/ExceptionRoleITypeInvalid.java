package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRoleITypeInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRoleITypeInvalid( String type ) {
		super("角色类别不合法，无法保存角色信息.Type:" + type );
	}
}
