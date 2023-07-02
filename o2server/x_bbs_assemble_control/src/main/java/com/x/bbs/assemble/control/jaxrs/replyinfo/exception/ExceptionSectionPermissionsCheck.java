package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionPermissionsCheck extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionPermissionsCheck( Throwable e, String name, String section, String role ) {
		super("用户["+name+"]没有版块["+ section +"]中的权限"+ role +"检查发生异常！", e );
	}
}
