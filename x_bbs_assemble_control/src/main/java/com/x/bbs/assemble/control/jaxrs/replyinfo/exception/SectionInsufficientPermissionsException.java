package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.exception.PromptException;

public class SectionInsufficientPermissionsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SectionInsufficientPermissionsException( String name, String role ) {
		super("用户没有版块["+ name +"]中的权限"+ role +"！" );
	}
}
