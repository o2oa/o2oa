package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ForumPermissionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumPermissionException( String forum, String role ) {
		super("用户没有论坛分区["+ forum +"]中的权限"+ role +"！" );
	}
}
