package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class SectionPermissionsCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionPermissionsCheckException( Throwable e, String name, String section, String role ) {
		super("用户["+name+"]没有版块["+ section +"]中的权限"+ role +"检查发生异常！", e );
	}
}
