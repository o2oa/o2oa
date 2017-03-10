package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoListByObjectException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoListByObjectException( Throwable e, String name, String type ) {
		super("系统在根据被角色绑定对象的唯一标识查询角色信息列表时发生异常.Name:" + name + ", Type:" + type, e);
	}
}
