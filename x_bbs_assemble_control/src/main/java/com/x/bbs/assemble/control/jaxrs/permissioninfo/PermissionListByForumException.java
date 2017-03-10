package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import com.x.base.core.exception.PromptException;

class PermissionListByForumException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PermissionListByForumException( Throwable e, String forumId ) {
		super("根据指定的论坛分区列示所有的权限信息时时发生异常.ForumId:" + forumId, e );
	}
}
