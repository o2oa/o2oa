package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoWrapInException( Throwable e ) {
		super("将用户传入的信息转换为一个角色信息对象时发生异常。", e );
	}
}
