package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoListBySectionIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoListBySectionIdException( Throwable e, String id ) {
		super("系统在根据版块ID查询角色信息列表时发生异常.Section:" + id, e);
	}
}
