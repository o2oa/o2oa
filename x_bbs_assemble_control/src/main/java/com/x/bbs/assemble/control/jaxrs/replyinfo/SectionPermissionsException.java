package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class SectionPermissionsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionPermissionsException( String name, String role ) {
		super("用户没有版块["+ name +"]中的权限"+ role +"！" );
	}
}
