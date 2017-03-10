package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class RoleInfoQueryByCodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RoleInfoQueryByCodeException( Throwable e, String code ) {
		super("系统在根据编码获取BBS角色信息时发生异常！Code:" + code, e );
	}
}
