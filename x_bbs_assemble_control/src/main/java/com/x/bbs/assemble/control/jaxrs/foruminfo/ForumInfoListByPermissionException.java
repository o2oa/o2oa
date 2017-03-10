package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumInfoListByPermissionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumInfoListByPermissionException( Throwable e ) {
		super("系统在根据用户权限获取BBS论坛分区信息列表时发生异常！", e );
	}
}
