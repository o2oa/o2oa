package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRoleInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRoleInfoNotExists( String id ) {
		super("指定ID的BBS角色不存在.ID:" + id );
	}
}
