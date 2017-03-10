package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoListByForumIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoListByForumIdException( Throwable e, String id ) {
		super("系统在根据论坛分区ID查询角色信息列表时发生异常.Forum:" + id, e);
	}
}
