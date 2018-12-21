package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumInsufficientPermission extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumInsufficientPermission( String forum, String role ) {
		super("用户没有论坛分区["+ forum +"]中的权限"+ role +"！" );
	}
}
