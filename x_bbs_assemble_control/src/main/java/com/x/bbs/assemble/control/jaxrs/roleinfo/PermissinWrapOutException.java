package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class PermissinWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PermissinWrapOutException( Throwable e ) {
		super("系统在转换所有BBS权限信息为输出对象时发生异常.", e );
	}
}
